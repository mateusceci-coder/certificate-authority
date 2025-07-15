import type { Route } from "./+types/home";
import { useState } from "react";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "~/components/ui/tabs";
import {
  Card,
  CardHeader,
  CardTitle,
  CardDescription,
  CardContent,
} from "~/components/ui/card";
import { Label } from "~/components/ui/label";
import { Input } from "~/components/ui/input";
import { Button } from "~/components/ui/button";
import { Separator } from "~/components/ui/separator";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "~/components/ui/select";
import {
  FileText,
  Shield,
  Search,
  Info,
  CheckCircle,
  XCircle,
  Download,
  BadgeIcon as Certificate,
  Upload,
  AlertCircle,
} from "lucide-react";
import { Textarea } from "~/components/ui/textarea";
import { Alert, AlertDescription } from "~/components/ui/alert";
import { Badge } from "~/components/ui/badge";
import { api } from "~/api";

export function meta({}: Route.MetaArgs) {
  return [
    { title: "New React Router App" },
    { name: "description", content: "Welcome to React Router!" },
  ];
}

export default function Home() {
  const [activeTab, setActiveTab] = useState("issue");
  const [loading, setLoading] = useState(false);
  const [result, setResult] = useState<any>(null);
  const [error, setError] = useState<string | null>(null);

  // Certificate issuance form state
  const [certForm, setCertForm] = useState({
    commonName: "",
    organization: "",
    organizationalUnit: "",
    country: "",
    state: "",
    locality: "",
    email: "",
    subjectAltNames: "",
  });

  // PDF signing form state
  const [pdfFile, setPdfFile] = useState<File | null>(null);
  const [signatureReason, setSignatureReason] = useState("");
  const [signatureLocation, setSignatureLocation] = useState("");

  // Signature verification state
  const [verifyFile, setVerifyFile] = useState<File | null>(null);

  // Certificate lookup state
  const [serialNumber, setSerialNumber] = useState("");

  const handleCertificateIssue = async () => {
    setLoading(true);
    setError(null);
    try {
      // Simulate API call
      await new Promise((resolve) => setTimeout(resolve, 2000));
      setResult({
        type: "certificate",
        serialNumber: "1A2B3C4D5E6F7890",
        commonName: certForm.commonName,
        issuer: "Your CA",
        validFrom: new Date().toISOString(),
        validTo: new Date(
          Date.now() +
            Number.parseInt(certForm.validityDays) * 24 * 60 * 60 * 1000
        ).toISOString(),
        status: "issued",
      });
    } catch (err) {
      setError("Failed to issue certificate");
    } finally {
      setLoading(false);
    }
  };

  const handlePdfSign = async () => {
    if (!pdfFile) {
      setError("Please select a PDF file");
      return;
    }
    setLoading(true);
    setError(null);
    try {
      await new Promise((resolve) => setTimeout(resolve, 2000));
      setResult({
        type: "pdf_signed",
        filename: pdfFile.name,
        signedAt: new Date().toISOString(),
        reason: signatureReason,
        location: signatureLocation,
        status: "signed",
      });
    } catch (err) {
      setError("Failed to sign PDF");
    } finally {
      setLoading(false);
    }
  };

  const handleSignatureVerify = async () => {
    if (!verifyFile) {
      setError("Please select a file to verify");
      return;
    }
    setLoading(true);
    setError(null);
    try {
      await new Promise((resolve) => setTimeout(resolve, 1500));
      setResult({
        type: "signature_verified",
        filename: verifyFile.name,
        valid: Math.random() > 0.3,
        signer: "John Doe",
        signedAt: "2024-01-15T10:30:00Z",
        certificateSerial: "1A2B3C4D5E6F7890",
      });
    } catch (err) {
      setError("Failed to verify signature");
    } finally {
      setLoading(false);
    }
  };

  const handleCertificateLookup = async () => {
    if (!serialNumber) {
      setError("Please enter a serial number");
      return;
    }
    setLoading(true);
    setError(null);
    try {
      await new Promise((resolve) => setTimeout(resolve, 1000));
      setResult({
        type: "certificate_lookup",
        serialNumber: serialNumber,
        commonName: "example.com",
        organization: "Example Corp",
        issuer: "Your CA",
        validFrom: "2024-01-01T00:00:00Z",
        validTo: "2025-01-01T00:00:00Z",
        status: "active",
        keyUsage: ["digitalSignature", "keyEncipherment"],
      });
    } catch (err) {
      setError("Certificate not found");
    } finally {
      setLoading(false);
    }
  };

  const handleGetCAInfo = async () => {
    setLoading(true);
    setError(null);
    try {
      const { data } = await api.getCaInfo();
      console.log("ca-info", data);
      setResult({
        type: "ca_info",
        name: "Fake Bry",
        issuer: data.issuer,
        serialNumber: data.serialNumber,
        validFrom: data.notBefore,
        validTo: data.notAfter,
        algorithm: data.algorithm,
        status: data.status,
      });
    } catch (err) {
      setError("Failed to get CA information");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50 p-4">
      <div className="max-w-6xl mx-auto">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">
            Certificate Authority
          </h1>
          <p className="text-gray-600">
            Manage certificates, sign documents, and verify signatures
          </p>
        </div>

        <Tabs
          value={activeTab}
          onValueChange={setActiveTab}
          className="space-y-6"
        >
          <TabsList className="grid w-full grid-cols-5">
            <TabsTrigger value="issue" className="flex items-center gap-2">
              <Certificate className="w-4 h-4" />
              Issue Certificate
            </TabsTrigger>
            <TabsTrigger value="sign" className="flex items-center gap-2">
              <FileText className="w-4 h-4" />
              Sign PDF
            </TabsTrigger>
            <TabsTrigger value="verify" className="flex items-center gap-2">
              <Shield className="w-4 h-4" />
              Verify Signature
            </TabsTrigger>
            <TabsTrigger value="lookup" className="flex items-center gap-2">
              <Search className="w-4 h-4" />
              Certificate Lookup
            </TabsTrigger>
            <TabsTrigger value="info" className="flex items-center gap-2">
              <Info className="w-4 h-4" />
              CA Info
            </TabsTrigger>
          </TabsList>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
            <div className="lg:col-span-2">
              <TabsContent value="issue" className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Issue X.509 Certificate</CardTitle>
                    <CardDescription>
                      Fill out the certificate details to issue a new X.509
                      certificate
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      <div className="space-y-2">
                        <Label htmlFor="cn">Common Name (CN) *</Label>
                        <Input
                          id="cn"
                          placeholder="example.com"
                          value={certForm.commonName}
                          onChange={(e) =>
                            setCertForm({
                              ...certForm,
                              commonName: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="email">Email Address</Label>
                        <Input
                          id="email"
                          type="email"
                          placeholder="admin@example.com"
                          value={certForm.email}
                          onChange={(e) =>
                            setCertForm({ ...certForm, email: e.target.value })
                          }
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="org">Organization (O)</Label>
                        <Input
                          id="org"
                          placeholder="Example Corp"
                          value={certForm.organization}
                          onChange={(e) =>
                            setCertForm({
                              ...certForm,
                              organization: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="ou">Organizational Unit (OU)</Label>
                        <Input
                          id="ou"
                          placeholder="IT Department"
                          value={certForm.organizationalUnit}
                          onChange={(e) =>
                            setCertForm({
                              ...certForm,
                              organizationalUnit: e.target.value,
                            })
                          }
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="country">Country (C)</Label>
                        <Input
                          id="country"
                          placeholder="US"
                          maxLength={2}
                          value={certForm.country}
                          onChange={(e) =>
                            setCertForm({
                              ...certForm,
                              country: e.target.value.toUpperCase(),
                            })
                          }
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="state">State/Province (ST)</Label>
                        <Input
                          id="state"
                          placeholder="California"
                          value={certForm.state}
                          onChange={(e) =>
                            setCertForm({ ...certForm, state: e.target.value })
                          }
                        />
                      </div>
                      <div className="space-y-2">
                        <Label htmlFor="locality">Locality (L)</Label>
                        <Input
                          id="locality"
                          placeholder="San Francisco"
                          value={certForm.locality}
                          onChange={(e) =>
                            setCertForm({
                              ...certForm,
                              locality: e.target.value,
                            })
                          }
                        />
                      </div>
                    </div>

                    <Button
                      onClick={handleCertificateIssue}
                      disabled={loading || !certForm.commonName}
                      className="w-full"
                    >
                      {loading ? "Issuing Certificate..." : "Issue Certificate"}
                    </Button>
                  </CardContent>
                </Card>
              </TabsContent>

              <TabsContent value="sign" className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Sign PDF Document</CardTitle>
                    <CardDescription>
                      Upload a PDF document to digitally sign it with your CA
                      certificate
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="pdf-upload">PDF Document</Label>
                      <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                        <Upload className="w-8 h-8 mx-auto mb-2 text-gray-400" />
                        <Input
                          id="pdf-upload"
                          type="file"
                          accept=".pdf"
                          onChange={(e) =>
                            setPdfFile(e.target.files?.[0] || null)
                          }
                          className="hidden"
                        />
                        <Label htmlFor="pdf-upload" className="cursor-pointer">
                          {pdfFile
                            ? pdfFile.name
                            : "Click to upload PDF or drag and drop"}
                        </Label>
                      </div>
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="reason">Signature Reason</Label>
                      <Input
                        id="reason"
                        placeholder="Document approval"
                        value={signatureReason}
                        onChange={(e) => setSignatureReason(e.target.value)}
                      />
                    </div>

                    <div className="space-y-2">
                      <Label htmlFor="location">Signature Location</Label>
                      <Input
                        id="location"
                        placeholder="San Francisco, CA"
                        value={signatureLocation}
                        onChange={(e) => setSignatureLocation(e.target.value)}
                      />
                    </div>

                    <Button
                      onClick={handlePdfSign}
                      disabled={loading || !pdfFile}
                      className="w-full"
                    >
                      {loading ? "Signing PDF..." : "Sign PDF"}
                    </Button>
                  </CardContent>
                </Card>
              </TabsContent>

              <TabsContent value="verify" className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Verify Digital Signature</CardTitle>
                    <CardDescription>
                      Upload a signed document to verify its digital signature
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="verify-upload">Signed Document</Label>
                      <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center">
                        <Shield className="w-8 h-8 mx-auto mb-2 text-gray-400" />
                        <Input
                          id="verify-upload"
                          type="file"
                          onChange={(e) =>
                            setVerifyFile(e.target.files?.[0] || null)
                          }
                          className="hidden"
                        />
                        <Label
                          htmlFor="verify-upload"
                          className="cursor-pointer"
                        >
                          {verifyFile
                            ? verifyFile.name
                            : "Click to upload signed document"}
                        </Label>
                      </div>
                    </div>

                    <Button
                      onClick={handleSignatureVerify}
                      disabled={loading || !verifyFile}
                      className="w-full"
                    >
                      {loading ? "Verifying Signature..." : "Verify Signature"}
                    </Button>
                  </CardContent>
                </Card>
              </TabsContent>

              <TabsContent value="lookup" className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Certificate Lookup</CardTitle>
                    <CardDescription>
                      Search for a certificate by its serial number
                    </CardDescription>
                  </CardHeader>
                  <CardContent className="space-y-4">
                    <div className="space-y-2">
                      <Label htmlFor="serial">Certificate Serial Number</Label>
                      <Input
                        id="serial"
                        placeholder="1A2B3C4D5E6F7890"
                        value={serialNumber}
                        onChange={(e) => setSerialNumber(e.target.value)}
                      />
                    </div>

                    <Button
                      onClick={handleCertificateLookup}
                      disabled={loading || !serialNumber}
                      className="w-full"
                    >
                      {loading ? "Searching..." : "Lookup Certificate"}
                    </Button>
                  </CardContent>
                </Card>
              </TabsContent>

              <TabsContent value="info" className="space-y-6">
                <Card>
                  <CardHeader>
                    <CardTitle>Certificate Authority Information</CardTitle>
                    <CardDescription>
                      View information about your Certificate Authority
                    </CardDescription>
                  </CardHeader>
                  <CardContent>
                    <Button
                      onClick={handleGetCAInfo}
                      disabled={loading}
                      className="w-full"
                    >
                      {loading ? "Loading..." : "Get CA Information"}
                    </Button>
                  </CardContent>
                </Card>
              </TabsContent>
            </div>

            {/* Results Panel */}
            <div className="lg:col-span-1">
              <Card className="sticky top-4">
                <CardHeader>
                  <CardTitle>Results</CardTitle>
                  <CardDescription>
                    Operation results will appear here
                  </CardDescription>
                </CardHeader>
                <CardContent>
                  {error && (
                    <Alert className="mb-4">
                      <AlertCircle className="h-4 w-4" />
                      <AlertDescription>{error}</AlertDescription>
                    </Alert>
                  )}

                  {result && (
                    <div className="space-y-4">
                      {result.type === "certificate" && (
                        <div className="space-y-3">
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-5 h-5 text-green-500" />
                            <span className="font-medium">
                              Certificate Issued
                            </span>
                          </div>
                          <Separator />
                          <div className="space-y-2 text-sm">
                            <div>
                              <strong>Serial:</strong> {result.serialNumber}
                            </div>
                            <div>
                              <strong>Common Name:</strong> {result.commonName}
                            </div>
                            <div>
                              <strong>Issuer:</strong> {result.issuer}
                            </div>
                            <div>
                              <strong>Valid From:</strong>{" "}
                              {new Date(result.validFrom).toLocaleDateString()}
                            </div>
                            <div>
                              <strong>Valid To:</strong>{" "}
                              {new Date(result.validTo).toLocaleDateString()}
                            </div>
                          </div>
                          <Button size="sm" className="w-full">
                            <Download className="w-4 h-4 mr-2" />
                            Download Certificate
                          </Button>
                        </div>
                      )}

                      {result.type === "pdf_signed" && (
                        <div className="space-y-3">
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-5 h-5 text-green-500" />
                            <span className="font-medium">PDF Signed</span>
                          </div>
                          <Separator />
                          <div className="space-y-2 text-sm">
                            <div>
                              <strong>File:</strong> {result.filename}
                            </div>
                            <div>
                              <strong>Signed At:</strong>{" "}
                              {new Date(result.signedAt).toLocaleString()}
                            </div>
                            {result.reason && (
                              <div>
                                <strong>Reason:</strong> {result.reason}
                              </div>
                            )}
                            {result.location && (
                              <div>
                                <strong>Location:</strong> {result.location}
                              </div>
                            )}
                          </div>
                          <Button size="sm" className="w-full">
                            <Download className="w-4 h-4 mr-2" />
                            Download Signed PDF
                          </Button>
                        </div>
                      )}

                      {result.type === "signature_verified" && (
                        <div className="space-y-3">
                          <div className="flex items-center gap-2">
                            {result.valid ? (
                              <CheckCircle className="w-5 h-5 text-green-500" />
                            ) : (
                              <XCircle className="w-5 h-5 text-red-500" />
                            )}
                            <span className="font-medium">
                              Signature {result.valid ? "Valid" : "Invalid"}
                            </span>
                          </div>
                          <Separator />
                          <div className="space-y-2 text-sm">
                            <div>
                              <strong>File:</strong> {result.filename}
                            </div>
                            <div>
                              <strong>Signer:</strong> {result.signer}
                            </div>
                            <div>
                              <strong>Signed At:</strong>{" "}
                              {new Date(result.signedAt).toLocaleString()}
                            </div>
                            <div>
                              <strong>Certificate:</strong>{" "}
                              {result.certificateSerial}
                            </div>
                          </div>
                          <Badge
                            variant={result.valid ? "default" : "destructive"}
                            className="w-full justify-center"
                          >
                            {result.valid
                              ? "Trusted Signature"
                              : "Untrusted Signature"}
                          </Badge>
                        </div>
                      )}

                      {result.type === "certificate_lookup" && (
                        <div className="space-y-3">
                          <div className="flex items-center gap-2">
                            <CheckCircle className="w-5 h-5 text-green-500" />
                            <span className="font-medium">
                              Certificate Found
                            </span>
                          </div>
                          <Separator />
                          <div className="space-y-2 text-sm">
                            <div>
                              <strong>Serial:</strong> {result.serialNumber}
                            </div>
                            <div>
                              <strong>Common Name:</strong> {result.commonName}
                            </div>
                            <div>
                              <strong>Organization:</strong>{" "}
                              {result.organization}
                            </div>
                            <div>
                              <strong>Issuer:</strong> {result.issuer}
                            </div>
                            <div>
                              <strong>Valid From:</strong>{" "}
                              {new Date(result.validFrom).toLocaleDateString()}
                            </div>
                            <div>
                              <strong>Valid To:</strong>{" "}
                              {new Date(result.validTo).toLocaleDateString()}
                            </div>
                            <div>
                              <strong>Status:</strong>{" "}
                              <Badge variant="default">{result.status}</Badge>
                            </div>
                          </div>
                        </div>
                      )}

                      {result.type === "ca_info" && (
                        <div className="space-y-3">
                          <div className="flex items-center gap-2">
                            <Info className="w-5 h-5 text-blue-500" />
                            <span className="font-medium">CA Information</span>
                          </div>
                          <Separator />
                          <div className="space-y-2 text-sm">
                            <div>
                              <strong>Name:</strong> {result.name}
                            </div>
                            <div>
                              <strong>Issuer:</strong> {result.issuer}
                            </div>
                            <div>
                              <strong>Serial:</strong> {result.serialNumber}
                            </div>
                            <div>
                              <strong>Algorithm:</strong> {result.algorithm}
                            </div>
                            <div>
                              <strong>Key Size:</strong> {result.keySize} bits
                            </div>
                            <div>
                              <strong>Valid From:</strong>{" "}
                              {new Date(result.validFrom).toLocaleDateString()}
                            </div>
                            <div>
                              <strong>Valid To:</strong>{" "}
                              {new Date(result.validTo).toLocaleDateString()}
                            </div>
                            <div>
                              <strong>Certificates Issued:</strong>{" "}
                              {result.certificatesIssued}
                            </div>
                            <div>
                              <strong>Status:</strong>{" "}
                              <Badge variant="default">{result.status}</Badge>
                            </div>
                          </div>
                        </div>
                      )}
                    </div>
                  )}

                  {!result && !error && (
                    <div className="text-center text-gray-500 py-8">
                      <Certificate className="w-12 h-12 mx-auto mb-2 opacity-50" />
                      <p>No results yet</p>
                      <p className="text-sm">
                        Perform an operation to see results
                      </p>
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </div>
        </Tabs>
      </div>
    </div>
  );
}
