package fr.esgi.secureupload.files.infrastructure.adapters.helpers;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import fr.esgi.secureupload.files.domain.port.FileStorageHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStream;

@Component
public class AzureFileStorageHandler implements FileStorageHandler {

    @Value("${secureupload.storage.connection_string}")
    private String connectionString;

    @Value("${secureupload.storage.container_name}")
    private String containerName;

    private BlobContainerClient connect(){

        BlobServiceClient client = new BlobServiceClientBuilder().connectionString(this.connectionString).buildClient();
        boolean exists = client.listBlobContainers()
                .stream()
                .anyMatch((c) -> c.getName().equals(this.containerName));

        if (!exists)
            return client.createBlobContainer(this.containerName);
        return client.getBlobContainerClient(this.containerName);
    }

    @Override
    public void deleteFile(String id) {
            BlobContainerClient client = connect();
            BlobClient blobClient = client.getBlobClient(id);
            blobClient.delete();
    }

    @Override
    public void storeFile(InputStream file, long size, String id) {
        BlobContainerClient client = connect();
        BlobClient blobClient = client.getBlobClient(id);
        blobClient.upload(file, size,true);
    }

    @Override
    public void downloadFile(String id, OutputStream stream) {
        BlobContainerClient client = connect();
        BlobClient blobClient = client.getBlobClient(id);
        blobClient.download(stream);
    }

    public void deleteAll(){
        BlobContainerClient client = connect();
        client.listBlobs().stream().forEach((blob) -> {
            client.getBlobClient(blob.getName()).delete();
        });
    }
}
