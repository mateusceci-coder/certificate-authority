package com.example.demo.controllers;


import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entities.Certificate;
import com.example.demo.exceptions.CertificateNotFoundException;
import com.example.demo.requests.SignatureValidationRequest;
import com.example.demo.services.CertificatesService;


@RestController
@RequestMapping("/api")
@org.springframework.web.bind.annotation.CrossOrigin(origins = {"http://localhost", "http://localhost:5173", "http://localhost:3000"})
public class CertificatesController {

    
    @Autowired
    private CertificatesService certificatesService;

    @GetMapping("/ca-info")
    public ResponseEntity<Map<String, Object>> getCaInfo() {
        try {
            X509Certificate caCertificate = certificatesService.getCaCertificate();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "CA Certificate loaded successfully");

            Map<String, String> data = new HashMap<>(); 

            String issuerDN = caCertificate.getIssuerX500Principal().getName();
            String cn = issuerDN.replaceAll(".*CN=([^,]+).*", "$1");
            data.put("issuer", cn);
            data.put("serialNumber", caCertificate.getSerialNumber().toString());
            data.put("notBefore", caCertificate.getNotBefore().toString());
            data.put("notAfter", caCertificate.getNotAfter().toString());
            data.put("algorithm", caCertificate.getSigAlgName());
            data.put("status", "active"); 
            response.put("data", data);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error loading CA certificate: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse); 
        }
    }

    @GetMapping("certificates/{serialNumber}")
    public ResponseEntity<Certificate> getCertificate(@org.springframework.web.bind.annotation.PathVariable String serialNumber) {
        try {
            java.util.Optional<Certificate> certificateOpt = certificatesService.getCertificateBySerialNumber(serialNumber);

            
            return ResponseEntity.ok(certificateOpt.get());
        } catch (CertificateNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/issue-certificate")
    public ResponseEntity<Map<String, Object>> issueCertificate(@RequestBody String csrPem) {
        try {
            String certificate = certificatesService.issueCertificate(csrPem);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("certificate", certificate);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "Error processing CSR: " + e.getMessage());
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PostMapping("/validate-signature")
    public ResponseEntity<Map<String, Object>> validateSignature(@RequestBody SignatureValidationRequest request) {
        try {
            Map<String, Object> response = certificatesService.validateSignature(request);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Error validating signature: " + e.getMessage());
            response.put("certificateValid", false);
            response.put("signatureValid", false);
            
            return ResponseEntity.badRequest().body(response);
        }
    }
}
