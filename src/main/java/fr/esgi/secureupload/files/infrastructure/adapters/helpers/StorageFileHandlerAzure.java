package fr.esgi.secureupload.files.infrastructure.adapters.helpers;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import fr.esgi.secureupload.files.domain.port.StorageFileHandler;

import java.io.InputStream;

public class StorageFileHandlerAzure implements StorageFileHandler {

    private String connectionString;

    private String containerName;

    public StorageFileHandlerAzure(String connectionString, String containerName){
        this.connectionString = connectionString;
        this.containerName = containerName;
    }

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
    public String storeFile(InputStream file, long size, String id)  {
        BlobContainerClient client = connect();
        BlobClient blobClient = client.getBlobClient(id);
        blobClient.upload(file, size,true);
        return blobClient.getBlobUrl();
    }
}
