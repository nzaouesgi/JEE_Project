provider azurerm {
  version = ">=2.0.0"
  features {}

  subscription_id = "034e914f-d017-40b6-9bbf-69efc3549676"
  client_id       = "369d7c46-14b7-4380-b2be-f180ac07ac37"
  client_secret   = var.azure_secret
  tenant_id       = "c371d4f5-b34f-4b06-9e66-517fed904220"
}

resource random_string jwt {
  length = 32
  upper = false
  number = false
  special = false
}

variable scale_count {
  default = 2
}

variable main_prefix {
  description = "The main prefix to append for every resource name"
  default = "secureupload"
}

variable mysql_root_password {
  description="MYSQL server main password"
}
variable azure_secret {
  description = "The password for the Azure service principal"
}
variable admin_email {
  default = "secureupload@secureupload.com"
  description = "The email of the application administrator account"
}
variable admin_password {
  description = "The password of the application administrator account"
}
variable smtp_host {
  description = "The host of the SMTP server to use"
}
variable smtp_port {
  description = "The port of the SMTP server to use"
}
variable smtp_user {
  description = "The user to use for sending emails"
}
variable smtp_password {
  description = "The password of the SMTP user"
}

# Create a resource group
resource azurerm_resource_group secure_upload {
  name     = "secureupload"
  location = "West Europe"
}

# MYSQL
resource azurerm_mysql_server mysql {
  name                = "${var.main_prefix}-mysql-server"
  location            = azurerm_resource_group.secure_upload.location
  resource_group_name = azurerm_resource_group.secure_upload.name

  sku_name   = "B_Gen5_2"
  storage_mb = 5120
  version    = "5.7"

  administrator_login          = "secureupload"
  administrator_login_password = var.mysql_root_password

  public_network_access_enabled     = true
  ssl_enforcement_enabled           = true
}

# This firewall rule allows MySQL to communicate with other resources such as App services etc...
resource azurerm_mysql_firewall_rule mysql_firewall {
  name                = "mysqlfirewall"
  resource_group_name = azurerm_resource_group.secure_upload.name
  server_name         = azurerm_mysql_server.mysql.name
  start_ip_address    = "0.0.0.0"
  end_ip_address      = "0.0.0.0"

  depends_on = [ azurerm_mysql_server.mysql ]
}

# This is the main application database
resource azurerm_mysql_database database {
  name                = "secureupload"
  resource_group_name = azurerm_resource_group.secure_upload.name
  server_name         = azurerm_mysql_server.mysql.name
  charset             = "utf8"
  collation           = "utf8_unicode_ci"

  depends_on = [ azurerm_mysql_server.mysql ]
}

# this storage account will have only one container, it will contain the uploaded files.
resource azurerm_storage_account storage_account {
  name                     = "${var.main_prefix}storage"
  resource_group_name      = azurerm_resource_group.secure_upload.name
  location                 = azurerm_resource_group.secure_upload.location
  account_tier             = "Standard"
  account_replication_type = "LRS"
  account_kind             = "StorageV2"
}

resource azurerm_storage_container file_storage {
  name                  = "filestorage"
  storage_account_name  = azurerm_storage_account.storage_account.name
  container_access_type = "private"

  depends_on = [ azurerm_storage_account.storage_account ]
}

# Our container registry with the webhook
resource azurerm_container_registry container_registry {
  name                = "${var.main_prefix}registry"
  resource_group_name = azurerm_resource_group.secure_upload.name
  location            = azurerm_resource_group.secure_upload.location
  sku                 = "Basic"
  admin_enabled       = true

  # build & push image
  provisioner "local-exec" {
    command = "az acr build --image secureupload:latest --registry ${self.name} ."
  }
}

resource azurerm_container_registry_webhook ci_webhook {

  count = var.scale_count

  location = azurerm_resource_group.secure_upload.location
  resource_group_name = azurerm_resource_group.secure_upload.name
  registry_name = azurerm_container_registry.container_registry.name

  name            = "${var.main_prefix}ciwebhook${count.index}"
  service_uri     = "https://${azurerm_app_service.app_service[count.index].site_credential[0].username}:${azurerm_app_service.app_service[count.index].site_credential[0].password}@${azurerm_app_service.app_service[count.index].name}.scm.azurewebsites.net/docker/hook"
  actions = [ "push" ]
  status = "enabled"
  scope = "secureupload:latest"

  depends_on = [ azurerm_container_registry.container_registry ]
}

# Front door
resource azurerm_frontdoor front_door {
  name = "${var.main_prefix}-frontdoor"
  resource_group_name = azurerm_resource_group.secure_upload.name

  frontend_endpoint {
    name                              = "${var.main_prefix}-frontdoor"
    host_name                         = "${var.main_prefix}-frontdoor.azurefd.net"
    custom_https_provisioning_enabled = false
  }

  routing_rule {

    name               = "web"
    accepted_protocols = [ "Https" ]
    patterns_to_match  = [ "/*" ]
    frontend_endpoints = [ "${var.main_prefix}-frontdoor" ]

    forwarding_configuration {
      forwarding_protocol = "MatchRequest"
      backend_pool_name   = "secure-upload-backend-pool"
    }
  }

  backend_pool {

    name = "secure-upload-backend-pool"

    load_balancing_name = "load-balancer"
    health_probe_name   = "health-check"

    dynamic backend {

      for_each = azurerm_app_service.app_service

      content {
        host_header = backend.value.default_site_hostname
        address = backend.value.default_site_hostname
        http_port   = 80
        https_port  = 443
      }
    }
  }

  backend_pool_load_balancing {
    name = "load-balancer"
  }

  backend_pool_health_probe {
    name = "health-check"
    path = "/status"
    protocol = "Https"
    interval_in_seconds = 10
    probe_method = "HEAD"
    enabled = true
  }

  enforce_backend_pools_certificate_name_check = true
  depends_on = [ azurerm_app_service.app_service ]
}

# Main Spring API
resource azurerm_app_service_plan service_plan {

  count = var.scale_count

  name                = "${var.main_prefix}-backend-service-plan-${count.index}"
  location            = azurerm_resource_group.secure_upload.location
  resource_group_name = azurerm_resource_group.secure_upload.name

  kind = "Linux"

  # must be set when using Linux
  reserved = true

  # most basic SKU
  sku {
    tier = "Basic"
    size = "B1"
  }
}

resource azurerm_app_service app_service {

  count = var.scale_count

  name                = "${var.main_prefix}-appservice-${count.index}"
  location            = azurerm_resource_group.secure_upload.location
  resource_group_name = azurerm_resource_group.secure_upload.name
  app_service_plan_id = azurerm_app_service_plan.service_plan[count.index].id

  site_config {
    app_command_line = ""
    linux_fx_version = "DOCKER|${azurerm_container_registry.container_registry.name}.azurecr.io/secureupload:latest"
    always_on        = true
  }

  app_settings = {

    WEBSITES_ENABLE_APP_SERVICE_STORAGE = "false"

    DOCKER_REGISTRY_SERVER_URL          = "https://${azurerm_container_registry.container_registry.name}.azurecr.io"
    DOCKER_REGISTRY_SERVER_USERNAME     = azurerm_container_registry.container_registry.admin_username
    DOCKER_REGISTRY_SERVER_PASSWORD     = azurerm_container_registry.container_registry.admin_password

    WEBSITES_PORT = "8080"

    # These are app specific environment variables
    SPRING_PROFILES_ACTIVE     = "prod"

    MYSQL_URL = "jdbc:mysql://${azurerm_mysql_server.mysql.fqdn}:3306/${azurerm_mysql_database.database.name}?useSSL=true&requireSSL=false&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC"
    MYSQL_PASSWORD = azurerm_mysql_server.mysql.administrator_login_password
    MYSQL_USER = "${azurerm_mysql_server.mysql.administrator_login}@${azurerm_mysql_server.mysql.name}"

    SMTP_HOST = var.smtp_host
    SMTP_PASSWORD = var.smtp_password
    SMTP_PORT = var.smtp_port
    SMTP_USER = var.smtp_user

    ADMIN_EMAIL = var.admin_email
    ADMIN_PASSWORD = var.admin_password

    JWT_SECRET = random_string.jwt.result

    STORAGE_ACCOUNT_CONNECTION_STRING = azurerm_storage_account.storage_account.primary_connection_string
    BLOB_CONTAINER_NAME = azurerm_storage_container.file_storage.name
  }

  depends_on = [
    azurerm_app_service_plan.service_plan,
    azurerm_container_registry.container_registry,
    azurerm_mysql_server.mysql,
    azurerm_mysql_database.database,
    azurerm_storage_account.storage_account,
    azurerm_storage_container.file_storage
  ]

}