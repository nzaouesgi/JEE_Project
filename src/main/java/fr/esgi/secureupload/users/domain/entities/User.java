package fr.esgi.secureupload.users.domain.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.esgi.secureupload.common.entities.BaseEntity;

public class User extends BaseEntity {

    public static final String [] PRIVATE_FIELDS = new String [] { "password", "recoveryToken", "confirmationToken" };

    public static final int PASSWORD_MIN_LENGTH = 8;
    public static final int PASSWORD_MAX_LENGTH = 100;

    /* Used for login */
    private String email = null;

    /* password hash */
    @JsonIgnore
    private String password = null;

    private boolean isAdmin = false;

    /* If the user's mail address has been confirmed */
    private boolean isConfirmed = false;

    /* Random token sent to user's mail address to confirm it. */
    @JsonIgnore
    private String confirmationToken = null;

    /* Password recovery token sent to user's mail address, null when no reset request has been made. */
    @JsonIgnore
    private String recoveryToken = null;

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAdmin() {
        return this.isAdmin;
    }

    public void setAdmin(boolean admin) {
        this.isAdmin = admin;
    }

    public boolean isConfirmed() {
        return this.isConfirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.isConfirmed = confirmed;
    }

    public String getConfirmationToken() {
        return this.confirmationToken;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public String getRecoveryToken() {
        return this.recoveryToken;
    }

    public void setRecoveryToken(String recoveryToken) {
        this.recoveryToken = recoveryToken;
    }

    public static User.Builder builder (){
        return new User.Builder();
    }

    public static class Builder extends BaseEntity.Builder<Builder> {

        private String email;
        private String password;
        private boolean isAdmin;
        private boolean isConfirmed;
        private String confirmationToken;
        private String recoveryToken;

        private Builder (){ }

        public Builder email (String email){
            this.email = email;
            return this;
        }

        public Builder password (String password){
            this.password = password;
            return this;
        }

        public Builder confirmed (boolean isConfirmed){
            this.isConfirmed = isConfirmed;
            return this;
        }

        public Builder admin (boolean isAdmin){
            this.isAdmin = isAdmin;
            return this;
        }

        public Builder confirmationToken (String confirmationToken){
            this.confirmationToken = confirmationToken;
            return this;
        }

        public Builder recoveryToken (String recoveryToken){
            this.recoveryToken = recoveryToken;
            return this;
        }

        public User build (){

            /* if (this.confirmationToken == null){
                throw new IllegalArgumentException("Confirmation token must not be null.");
            }

            if (this.email == null){
                throw new IllegalArgumentException("Email field cannot be null.");
            }

            if (this.password == null){
                throw new IllegalArgumentException("Password field cannot be null.");
            } */

            return new User(this);
        }
    }

    private User(Builder builder){
        super(builder);
        this.setEmail(builder.email);
        this.setPassword(builder.password);
        this.setConfirmed(builder.isConfirmed);
        this.setAdmin(builder.isAdmin);
        this.setConfirmationToken(builder.confirmationToken);
        this.setRecoveryToken(builder.recoveryToken);
    }
}
