package com.example.demo.entities;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "certificates")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Certificate {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "serial_number", unique = true, nullable = false, length = 64)
    private String serialNumber;
    
    @Column(name = "version")
    private Integer version;
    
    @Column(name = "subject_common_name", nullable = false, length = 512)
    private String subjectCommonName;

    @Column(name = "subject_organization_name", nullable = true, length = 512)
    private String subjectOrganizationName;

    @Column(name = "subject_organization_unit", nullable = true, length = 512)
    private String subjectOrganizationUnit;

    @Column(name = "subject_country", nullable = true, length = 2)  
    private String subjectCountry;

    @Column(name = "subject_state", nullable = true, length = 512)
    private String subjectState;

    @Column(name = "subject_locality", nullable = true, length = 512)
    private String subjectLocality;

    @Column(name = "subject_email", nullable = true, length = 512)
    private String subjectEmail;
    
    @Column(name = "signature_algorithm", length = 128) 
    private String signatureAlgorithm;
    
    @Column(name = "public_key_algorithm", length = 128)
    private String publicKeyAlgorithm;
    
    @Column(name = "certificate_pem", nullable = false, columnDefinition = "TEXT")
    private String certificatePem;
    
    @Lob
    @Column(name = "certificate_blob", nullable = false)
    private byte[] certificateBlob;
    
    @Column(name = "not_before", nullable = false)
    private LocalDateTime notBefore;
    
    @Column(name = "not_after", nullable = false)
    private LocalDateTime notAfter;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "status", nullable = false, length = 32)
    private String status = "ACTIVE";
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
} 