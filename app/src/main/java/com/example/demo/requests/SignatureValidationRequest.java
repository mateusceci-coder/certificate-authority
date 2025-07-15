package com.example.demo.requests;

public class SignatureValidationRequest {
    private String certificatePem;
        private String data;
        private String signature;

        public String getCertificatePem() {
            return certificatePem;
        }

        public void setCertificatePem(String certificatePem) {
            this.certificatePem = certificatePem;
        }

        public String getData() {
            return data;
        }

        public void setData(String data) {
            this.data = data;
        }

        public String getSignature() {
            return signature;
        }

        public void setSignature(String signature) {
            this.signature = signature;
        }
}
