package com.example.demo.controllers;


import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.example.demo.DTOs.CaCertificateInfoDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

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
    public ResponseEntity<CaCertificateInfoDTO> getCaInfo() {

        CaCertificateInfoDTO caCertificateInfoDTO = certificatesService.getCaCertificate();

        return ResponseEntity.ok(caCertificateInfoDTO);
    }

    @GetMapping("certificates/{serialNumber}")
    public ResponseEntity<Certificate> getCertificate(@PathVariable String serialNumber) {

        Certificate certificateOpt = certificatesService.getCertificateBySerialNumber(serialNumber);
        return ResponseEntity.ok(certificateOpt);

    }

    @PostMapping("/issue-certificate")
    public ResponseEntity<Map<String, Object>> issueCertificate(@RequestBody String csrPem) {

        String certificate = certificatesService.issueCertificate(csrPem);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("certificate", certificate);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/validate-signature")
    public ResponseEntity<Map<String, Object>> validateSignature(@RequestBody SignatureValidationRequest request) {

        Map<String, Object> response = certificatesService.validateSignature(request);

        return ResponseEntity.ok(response);

    }
}
