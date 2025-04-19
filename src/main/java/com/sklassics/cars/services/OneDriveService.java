//package com.sklassics.cars.services;

//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import com.azure.identity.ClientSecretCredential;
//import com.azure.identity.ClientSecretCredentialBuilder;
//import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
//import com.microsoft.graph.http.GraphServiceException;
//import com.microsoft.graph.models.DriveItem;
//import com.microsoft.graph.models.Folder;
//import com.microsoft.graph.requests.GraphServiceClient;
//import okhttp3.Request;
//import java.util.*;
//@Service
//public class OneDriveService {
//
//    @Value("${onedrive.client-id}")
//    private String clientId;
//
//    @Value("${onedrive.client-secret}")
//    private String clientSecret;
//
//    @Value("${onedrive.tenant-id}")
//    private String tenantId;
//
//    private final String userEmail = "revanthGundabattina@Sklassicstechnologiesprivat.onmicrosoft.com";
//
//    private final String[] allowedExtensions = {".jpg", ".jpeg", ".png"};
//
//    private GraphServiceClient<Request> getGraphClient() {
//        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
//                .clientId(clientId)
//                .clientSecret(clientSecret)
//                .tenantId(tenantId)
//                .build();
//
//        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
//                Collections.singletonList("https://graph.microsoft.com/.default"), credential);
//
//        return GraphServiceClient.builder()
//                .authenticationProvider(authProvider)
//                .buildClient();
//    }
//
//    public List<String> uploadCarImages(Long carId, List<MultipartFile> images) throws Exception {
//        GraphServiceClient<Request> graphClient = getGraphClient();
//        String carFolderPath = "Car/" + carId;
//
//        // Ensure the folder for this car exists
//        createFolderIfNotExists(graphClient, "Car", String.valueOf(carId));
//
//        List<String> uploadedUrls = new ArrayList<>();
//        for (MultipartFile image : images) {
//            String fileExtension = getFileExtension(image.getOriginalFilename());
//            if (!isValidExtension(fileExtension)) {
//                throw new Exception("Invalid file format. Allowed formats: .jpg, .jpeg, .png");
//            }
//
//            byte[] fileBytes = image.getBytes();
//            String fileName = System.currentTimeMillis() + fileExtension;
//            String uploadPath = String.format("%s/%s", carFolderPath, fileName);
//
//            graphClient.users(userEmail).drive().root().itemWithPath(uploadPath).content().buildRequest().put(fileBytes);
//            uploadedUrls.add(String.format("https://graph.microsoft.com/v1.0/users/%s/drive/root:/%s", userEmail, uploadPath));
//        }
//        return uploadedUrls;
//    }
//
//    private void createFolderIfNotExists(GraphServiceClient<Request> graphClient, String parentFolder, String folderName) throws Exception {
//        try {
//            graphClient.users(userEmail).drive().root().itemWithPath(parentFolder + "/" + folderName).buildRequest().get();
//        } catch (GraphServiceException e) {
//            if (e.getResponseCode() == 404) {
//                DriveItem folder = new DriveItem();
//                folder.name = folderName;
//                folder.folder = new Folder();
//                graphClient.users(userEmail).drive().root().itemWithPath(parentFolder).children().buildRequest().post(folder);
//            } else {
//                throw new Exception("Error creating folder: " + e.getMessage());
//            }
//        }
//    }
//
//    private boolean isValidExtension(String extension) {
//        for (String ext : allowedExtensions) {
//            if (ext.equalsIgnoreCase(extension)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    private String getFileExtension(String fileName) {
//        return fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
//    }
//}

package com.sklassics.cars.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.http.GraphServiceException;
import com.microsoft.graph.models.DriveItem;
import com.microsoft.graph.models.DriveItemCreateLinkParameterSet;
import com.microsoft.graph.models.Folder;
import com.microsoft.graph.models.Permission;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import java.util.*;
import java.util.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


@Service
public class OneDriveService {

    @Value("${onedrive.client-id}")
    private String clientId;

    @Value("${onedrive.client-secret}")
    private String clientSecret;

    @Value("${onedrive.tenant-id}")
    private String tenantId;

    private final String userEmail = "revanthGundabattina@Sklassicstechnologiesprivat.onmicrosoft.com";

    private final String[] allowedExtensions = {".jpg", ".jpeg", ".png"};

    private GraphServiceClient<Request> getGraphClient() {
        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();

        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
                Collections.singletonList("https://graph.microsoft.com/.default"), credential);

        return GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();
    }

    public List<String> uploadCarImages(String folderName, List<MultipartFile> images) throws Exception {
        System.out.println("Starting image upload process...");

        GraphServiceClient<Request> graphClient = getGraphClient();
        System.out.println("Graph client initialized.");

        String carFolderPath = "Car/" + folderName; // Updated folder format
        System.out.println("Target folder path: " + carFolderPath);

        // Ensure folder exists in OneDrive
        System.out.println("Checking or creating folder: Car -> " + folderName);
        createFolderIfNotExists(graphClient, "Car", folderName);

        List<String> uploadedUrls = new ArrayList<>();
        for (MultipartFile image : images) {
            String originalFileName = image.getOriginalFilename();
            System.out.println("Processing file: " + originalFileName);

            String fileExtension = getFileExtension(originalFileName);
            System.out.println("File extension: " + fileExtension);

            if (!isValidExtension(fileExtension)) {
                System.out.println("Invalid file format: " + fileExtension);
                throw new Exception("Invalid file format. Allowed formats: .jpg, .jpeg, .png");
            }

            byte[] fileBytes = image.getBytes();
            String fileName = System.currentTimeMillis() + fileExtension; // Unique filename
            String uploadPath = String.format("%s/%s", carFolderPath, fileName);

            System.out.println("Uploading file to path: " + uploadPath);

            // Upload file to OneDrive
            graphClient.users(userEmail)
                    .drive()
                    .root()
                    .itemWithPath(uploadPath)
                    .content()
                    .buildRequest()
                    .put(fileBytes);

            String uploadedUrl = String.format("https://graph.microsoft.com/v1.0/users/%s/drive/root:/%s", userEmail, uploadPath);
            uploadedUrls.add(uploadedUrl);

            System.out.println("File uploaded successfully: " + uploadedUrl);
        }

        System.out.println("All files uploaded. Total: " + uploadedUrls.size());
        return uploadedUrls;
    }



    private void createFolderIfNotExists(GraphServiceClient<Request> graphClient, String parentFolder, String folderName) throws Exception {
        try {
            // Check if folder exists
            graphClient.users(userEmail)
                    .drive()
                    .root()
                    .itemWithPath(parentFolder + "/" + folderName)
                    .buildRequest()
                    .get();
        } catch (GraphServiceException e) {
            if (e.getResponseCode() == 404) {
                // Get parent folder DriveItem (e.g., "Car")
                DriveItem parent = graphClient.users(userEmail)
                        .drive()
                        .root()
                        .itemWithPath(parentFolder)
                        .buildRequest()
                        .get();

                // Prepare the folder to create
                DriveItem folder = new DriveItem();
                folder.name = folderName;
                folder.folder = new Folder();

                // Create inside parent
                graphClient.users(userEmail)
                        .drive()
                        .items(parent.id) // Use DriveItem ID here
                        .children()
                        .buildRequest()
                        .post(folder);
            } else {
                throw new Exception("Error checking or creating folder: " + e.getMessage());
            }
        }
    }
    
    public String uploadLicenseFile(String folderName, MultipartFile file) throws Exception {
        // Initialize GraphServiceClient (oneDrive API client)
        GraphServiceClient<Request> graphClient = getGraphClient();

        // Define file upload path on OneDrive (UserLicenses folder in this case)
        String licenseFolderPath = "License/" + folderName; 

        // Ensure folder exists in OneDrive
        createFolderIfNotExists(graphClient, "License", folderName);

        // Get the original file name and extension
        String originalFileName = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFileName);

        // Validate file format
        if (!isValidExtension(fileExtension)) {
            throw new Exception("Invalid file format. Allowed formats: .jpg, .jpeg, .png, .pdf");
        }

        // Convert MultipartFile to byte array
        byte[] fileBytes = file.getBytes();
        String fileName = System.currentTimeMillis() + fileExtension; // Generate unique file name
        String uploadPath = String.format("%s/%s", licenseFolderPath, fileName); // Set file path

        // Upload file to OneDrive
        graphClient.users(userEmail)
                .drive()
                .root()
                .itemWithPath(uploadPath)
                .content()
                .buildRequest()
                .put(fileBytes);

        // Generate uploaded file URL and return
        String uploadedUrl = String.format("https://graph.microsoft.com/v1.0/users/%s/drive/root:/%s", userEmail, uploadPath);
        return uploadedUrl;
    }

    // Helper methods like getGraphClient(), createFolderIfNotExists(), getFileExtension(), and isValidExtension()
    

    private boolean isValidExtension(String extension) {
        return Arrays.asList(allowedExtensions).contains(extension.toLowerCase());
    }

    private String getFileExtension(String fileName) {
        return fileName != null && fileName.contains(".") ? fileName.substring(fileName.lastIndexOf(".")) : "";
    }

    private String getAccessToken() throws Exception {
        String tokenUrl = "https://login.microsoftonline.com/" + tenantId + "/oauth2/v2.0/token";

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("scope", "https://graph.microsoft.com/.default");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(formData, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

        return response.getBody().get("access_token").toString();
    }


    public List<String> convertImagesToBase64(List<String> imageUrls) throws Exception {
        List<String> base64Images = new ArrayList<>();
        String accessToken = getAccessToken();
        System.out.println("Access Token fetched successfully: " + accessToken);

        System.out.println("Total image URLs received: " + imageUrls.size());
        System.out.println("Image URLs List: " + imageUrls);

        for (String imageUrl : imageUrls) {
            try {
                System.out.println("Fetching image from OneDrive: " + imageUrl);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", "Bearer " + accessToken);
                HttpEntity<String> entity = new HttpEntity<>(headers);
                RestTemplate restTemplate = new RestTemplate();
                
                ResponseEntity<byte[]> response = restTemplate.exchange(imageUrl, HttpMethod.GET, entity, byte[].class);

                System.out.println("HTTP Response Status: " + response.getStatusCode());
                System.out.println("Response Headers: " + response.getHeaders());

                if (response.getBody() != null) {
                    String contentType = response.getHeaders().getContentType().toString();
                    System.out.println("Fetched image content type: " + contentType);

                    String base64Prefix = "data:" + contentType + ";base64,";
                    String base64String = base64Prefix + Base64.getEncoder().encodeToString(response.getBody());

                    System.out.println("Image converted to Base64, length: " + base64String.length());
                    base64Images.add(base64String);
                } else {
                    System.err.println("Empty response body for image: " + imageUrl);
                }
            } catch (Exception e) {
                System.err.println("Error processing image from OneDrive: " + imageUrl + " - " + e.getMessage());
                e.printStackTrace();
            }
        }

        System.out.println("Total images converted to Base64: " + base64Images.size());
        return base64Images;
    }
    public String generateViewLink(String itemPath) throws Exception {
        GraphServiceClient<Request> graphClient = getGraphClient(); // Get your Graph Client instance

        // Define parameters for creating the public view link
        DriveItemCreateLinkParameterSet parameters = DriveItemCreateLinkParameterSet
                .newBuilder()
                .withType("view") // Type set to "view" to allow only viewing access
                .withScope("anonymous") // Public access, anyone with the link can view
                .build();

        // Create the link using the Microsoft Graph API
        Permission permission = graphClient
                .users(userEmail)
                .drive()
                .root()
                .itemWithPath(itemPath)
                .createLink(parameters)
                .buildRequest()
                .post();

        // If link creation is successful, return the view URL
        if (permission != null && permission.link != null) {
            return permission.link.webUrl;
        } else {
            throw new Exception("Failed to generate view link for: " + itemPath);
        }
    }
    
    
    public String generateDirectDownloadLink(String itemPath) throws Exception {
        GraphServiceClient<Request> graphClient = getGraphClient(); // Get your Graph Client instance

        DriveItem item = graphClient
                .users(userEmail)
                .drive()
                .root()
                .itemWithPath(itemPath)
                .buildRequest()
                .get();

        // Extract @microsoft.graph.downloadUrl from additional data
        if (item.additionalDataManager().containsKey("@microsoft.graph.downloadUrl")) {
            return item.additionalDataManager()
                       .get("@microsoft.graph.downloadUrl")
                       .getAsString();
        } else {
            throw new Exception("Failed to retrieve download URL for: " + itemPath);
        }
    }



}