package fr.esgi.secureupload.files.infrastructure.adapters.helpers;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import fr.esgi.secureupload.files.domain.port.StorageFileHandler;

import java.io.InputStream;

public class StorageFileHandlerImpl implements StorageFileHandler {

    private String connectStr;

    private String containerStr;

    public StorageFileHandlerImpl(String connectStr, String containerStr){
        this.connectStr = connectStr;
        this.containerStr = containerStr;
    }

    private BlobContainerClient connect(){
        BlobServiceClient client = new BlobServiceClientBuilder().connectionString(this.connectStr).buildClient();
        return client.getBlobContainerClient(this.containerStr);
    }

    @Override
    public boolean deleteFile(String id) {
        try{
            BlobContainerClient client = connect();
            BlobClient blobClient = client.getBlobClient(id);
            blobClient.delete();
        }
        catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;

    }

    @Override
    public boolean storeFile(InputStream file, long size, String id) {
        try {
            BlobContainerClient client = connect();
            BlobClient blobClient = client.getBlobClient(id);
            blobClient.upload(file, size,true);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
