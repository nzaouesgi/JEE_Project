# Secure upload
A secure file hosting service with built-in malware analysis.

## Launch app (docker-compose)

If you just want to launch the app with the required services:

```
docker-compose -f <PATH TO REPOSITORY>/compose-dev/docker-compose-run-with-services.yml up
```

If you want to launch only the services and debug the app from your IDE in a regular way , you will need to configure some environment variables into your IDE build/run settings (probably for both testing and spring run configs).

This one will work for IntelliJ in a local environment (just copy and paste it):

```
MYSQL_URL=jdbc:mysql://127.0.0.1:3306/secureupload;MYSQL_USER=secureupload;MYSQL_PASSWORD=DatabasePassword12345;SMTP_HOST=127.0.0.1;SMTP_PORT=1025;SMTP_USER=secureupload@secureupload.com;SMTP_PASSWORD=SmtpPassword12345;ADMIN_EMAIL=admin@secureupload.com;JWT_SECRET=SecretForDevOnly;AZURE_STORAGE_CONNECTION_STRING=DefaultEndpointsProtocol=https\;AccountName=devstoreaccount1\;AccountKey=Eby8vdM02xNOcqFlqUwJPLlmEtlCDXJ1OUzFT50uSRZ6IFsuFq2UVErCz4I6tq/K1SZFPTOtr/KBHBeksoGMGw==\;BlobEndpoint=https://127.0.0.1:10000/devstoreaccount1\;;AZURE_STORAGE_CONTAINER_NAME=my-container;ADMIN_PASSWORD=secureupload
```
Then you can launch the services with:
```
docker-compose -f <PATH TO REPOSITORY>/compose-dev/docker-compose-only-services.yml up
```

## Deploy app in Azure

The application can be directly deployed to an Azure environment in a very simple and reversible way.

You will need the following dependencies:

* Terraform
* Azure CLI
* Docker
* A valid Azure service principal

Once you installed these, you can use the following commands from the root of the repository.

1. Copy the file ```terraform.tfvars.example``` into ```terraform.tfvars``` within the repository root folder.

1. Edit the ```terraform.tfvars``` file with your custom values/secrets. 

    **WARNING**: You must never use any hardcoded value from the ```terraform.tfvars.example``` file. You must replace them all.
    
1. Log in into your service principal, this is needed because we use a custom provisioner to build and push the application image into our Azure container registry.
    ```
    az login --service-principal --username <SERVICE_PRINCIPAL_NAME> --password <SERVICE_PRINCIPAL_PASSWORD> --tenant <TENANT_ID>
    ```
1. Install Terraform script dependencies (could be done only once):
    ```
    terraform init
    ```
1. Plan your deployment (you might want to review the terraform plan command's output before):
    ```
   terraform plan -out deploy
    ```
1. Deploy the infrastructure:
   ```
   terraform apply "deploy"
   ```
1. If you want to destroy all resources:
   ```
   terraform destroy
   ```
## CI/CD

If you have a Drone CI/CD server, you can use or customize our drone.yml script in order to continuously deploy the application's image to your azure instances.

You need to configure ``registry``, ``username`` in the ``buildandpush`` step of the ``deploy`` pipeline. Also you will need to set the container registry admin's password as a secret in your Drone server (```AZURE_CONTAINER_REGISTRY_PASSWORD```)

You can easily find these values by checking out the container registry resource within the Azure portal.

Everytime a push is done on the master branch, the instances will be updated with the last image.