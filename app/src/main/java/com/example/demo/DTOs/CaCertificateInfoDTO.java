package com.example.demo.DTOs;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigInteger;
import java.util.Date;

@Getter
@AllArgsConstructor

public class CaCertificateInfoDTO {
    private final String status = "active";
    private String issuer;
    private BigInteger serialNumber;
    private Date notBefore;
    private Date notAfter;
    private String algorithm;
}
