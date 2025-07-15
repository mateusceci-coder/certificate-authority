package com.example.demo.services;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.ZoneId;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;
import java.util.Optional;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.jce.PKCS10CertificationRequest;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.entities.Certificate;
import com.example.demo.exceptions.CertificateNotFoundException;
import com.example.demo.repositories.CertificateRepository;
import com.example.demo.requests.SignatureValidationRequest;

@Service
public class CertificatesService {
    
    @Autowired
    private CertificateRepository certificateRepository;

    /**
     * Retrieves a certificate by its serial number
     * @param serialNumber the serial number to search for
     * @return Optional containing the certificate if found, empty otherwise
     */
    public Optional<Certificate> getCertificateBySerialNumber(String serialNumber) {
        Optional<Certificate> certificate = certificateRepository.findBySerialNumber(serialNumber);
        if (certificate.isEmpty()) {
            throw new CertificateNotFoundException("Certificate not found");
        }
        return certificate;
    }

    /**
     * Saves a certificate to the database
     * @param certificate the certificate to save
     * @return the saved certificate
     */
    public Certificate saveCertificate(Certificate certificate) {
        return certificateRepository.save(certificate);
    }

    public X509Certificate getCaCertificate() {
        return loadCACertificate();
    }

    public String issueCertificate(String csrPem) {
        PKCS10CertificationRequest csr = parseCsrPem(csrPem);

        X509Certificate x509Certificate = generateX509Certificate(csr);

        String signedCertificate = signX509Certificate(x509Certificate);
        
        saveCertificateToDatabase(x509Certificate, signedCertificate);

        return signedCertificate;
    }

    public Map<String, Object> validateSignature(SignatureValidationRequest request) {
        X509Certificate clientCertificate = parseCertificateFromPem(request.getCertificatePem());
            
        boolean isCertificateValid = verifyCertificateChain(clientCertificate);

        if (!isCertificateValid) {
            Map<String, Object> certificateValidation = new HashMap<>();
            certificateValidation.put("certificateValid", false);
            certificateValidation.put("message", "Certificate was not issued by this CA");
            return certificateValidation;
        }

        
        boolean isSignatureValid = verifySignature(
            request.getData(), 
            request.getSignature(), 
            clientCertificate.getPublicKey()
        );

        if (!isSignatureValid) {
            Map<String, Object> certificateValidation = new HashMap<>();
            certificateValidation.put("certificateValid", true);
            certificateValidation.put("signatureValid", false);
            return certificateValidation;
        }

        Map<String, Object> certificateValidation = new HashMap<>();
        certificateValidation.put("certificateValid", true);
        certificateValidation.put("signatureValid", true);
        return certificateValidation;
    }

    private PKCS10CertificationRequest parseCsrPem(String csrPem) {
        try (PEMParser pemParser = new PEMParser(new StringReader(csrPem))) {
            Object parsedObject = pemParser.readObject();
            if(parsedObject instanceof PKCS10CertificationRequest csr) {
                return csr;
            } else {
                throw new IllegalArgumentException("Invalid CSR format");
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSR: " + e.getMessage(), e);
        }
    }

    private X509Certificate loadCACertificate() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            try (FileInputStream fis = new FileInputStream("/certs/rootCA.crt")) {
                return (X509Certificate) certificateFactory.generateCertificate(fis);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load CA certificate: " + e.getMessage(), e);
        }
    }

    private PrivateKey loadCAPrivateKey() {
        try {
            try (FileInputStream fis = new FileInputStream("/certs/rootCA.key");
                 PemReader pemReader = new PemReader(new java.io.InputStreamReader(fis))) {
                
                PemObject pemObject = pemReader.readPemObject();
                byte[] keyBytes = pemObject.getContent();
                
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePrivate(keySpec);
            }   
        } catch (Exception e) {
            throw new RuntimeException("Failed to load CA private key: " + e.getMessage(), e);
        }
    }

    private X509Certificate generateX509Certificate(PKCS10CertificationRequest csr) {
        try {
            X509Certificate caCertificate = loadCACertificate();
            PrivateKey caPrivateKey = loadCAPrivateKey();

            X500Name subject = csr.getCertificationRequestInfo().getSubject();

            SubjectPublicKeyInfo publicKeyInfo = csr.getCertificationRequestInfo().getSubjectPublicKeyInfo();
            
            BigInteger serialNumber = generateUniqueSerialNumber();
            Date notBefore = new Date();
            Date notAfter = new Date(notBefore.getTime() + 365 * 24 * 60 * 60 * 1000L); 
            
            X500Name issuer = new X500Name(caCertificate.getSubjectX500Principal().getName());
            X509v3CertificateBuilder certificateBuilder = new X509v3CertificateBuilder(
                issuer, 
                serialNumber,
                notBefore,
                notAfter,
                subject, 
                publicKeyInfo 
            );
            
            ContentSigner contentSigner = new JcaContentSignerBuilder("SHA256WithRSA")
                .build(caPrivateKey);
            
            X509CertificateHolder certificateHolder = certificateBuilder.build(contentSigner);
            
            JcaX509CertificateConverter certificateConverter = new JcaX509CertificateConverter();
            return certificateConverter.getCertificate(certificateHolder);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate certificate: " + e.getMessage(), e);
        }
    }

    private String signX509Certificate(X509Certificate certificate) {
        try {
            StringWriter stringWriter = new StringWriter();
            try (JcaPEMWriter pemWriter = new JcaPEMWriter(stringWriter)) {
                pemWriter.writeObject(certificate);
            }
            return stringWriter.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert certificate to PEM: " + e.getMessage(), e);
        }
    }

    /**
     * Saves a certificate to the database as a blob
     */
    private void saveCertificateToDatabase(X509Certificate x509Certificate, String certificatePem) {
        try {
            Certificate certificate = new Certificate();
            
            byte[] certificateDer = x509Certificate.getEncoded();
            
            // Set basic certificate information
            certificate.setVersion(x509Certificate.getVersion());
            certificate.setSerialNumber(x509Certificate.getSerialNumber().toString());
            certificate.setSubjectCommonName(x509Certificate.getSubjectX500Principal().getName());
            certificate.setSubjectOrganizationName(x509Certificate.getSubjectX500Principal().getName());
            certificate.setSubjectOrganizationUnit(x509Certificate.getSubjectX500Principal().getName());
            certificate.setSubjectCountry(x509Certificate.getSubjectX500Principal().getName());
            certificate.setSubjectState(x509Certificate.getSubjectX500Principal().getName());
            certificate.setSubjectLocality(x509Certificate.getSubjectX500Principal().getName());
            certificate.setSubjectEmail(x509Certificate.getSubjectX500Principal().getName());
            certificate.setCertificatePem(certificatePem);
            certificate.setSignatureAlgorithm(x509Certificate.getSigAlgName());
            certificate.setPublicKeyAlgorithm(x509Certificate.getPublicKey().getAlgorithm());
            certificate.setCertificateBlob(certificateDer);
            
            certificate.setNotBefore(x509Certificate.getNotBefore().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
            certificate.setNotAfter(x509Certificate.getNotAfter().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDateTime());
            
            saveCertificate(certificate);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to save certificate to database: " + e.getMessage(), e);
        }
    }

       /**
     * Parses an X.509 certificate from PEM format string
     */
    private X509Certificate parseCertificateFromPem(String certificatePem) {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            try (StringReader reader = new StringReader(certificatePem);
                 java.io.ByteArrayInputStream bis = new java.io.ByteArrayInputStream(certificatePem.getBytes())) {
                return (X509Certificate) certificateFactory.generateCertificate(bis);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse certificate from PEM: " + e.getMessage(), e);
        }
    }


    /**
     * Verifies that a certificate was signed by our CA
     */
    private boolean verifyCertificateChain(X509Certificate certificate) {
        try {
            X509Certificate caCertificate = loadCACertificate();
            
            certificate.verify(caCertificate.getPublicKey());
            
            certificate.checkValidity();
            
            return certificate.getIssuerX500Principal().equals(caCertificate.getSubjectX500Principal());
            
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies a digital signature using the provided public key
     */
    private boolean verifySignature(String data, String signatureBase64, PublicKey publicKey) {
        try {
            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes("UTF-8"));
            
            byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);
            return signature.verify(signatureBytes);
            
        } catch (Exception e) {
            return false;
        }
    }

    private String calculateThumbprint(byte[] der, String algorithm) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hash = digest.digest(der);
        return HexFormat.of().formatHex(hash);
    }



        /**
     * Generates a cryptographically secure unique serial number for X.509 certificates.
     * Uses SecureRandom to ensure uniqueness and security.
     * @return A positive BigInteger serial number
     */
    private BigInteger generateUniqueSerialNumber() {
        SecureRandom secureRandom = new SecureRandom();
        
        byte[] serialBytes = new byte[8];
        secureRandom.nextBytes(serialBytes);
        
        serialBytes[0] &= 0x7F;
        
        BigInteger serialNumber = new BigInteger(1, serialBytes);
        
        if (serialNumber.equals(BigInteger.ZERO)) {
            serialNumber = BigInteger.ONE;
        }
        
        return serialNumber;
    }
}