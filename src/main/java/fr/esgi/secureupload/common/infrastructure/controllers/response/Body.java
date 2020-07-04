package fr.esgi.secureupload.common.infrastructure.controllers.response;

import java.util.Date;

@lombok.Getter
@lombok.Setter
public abstract class Body {
    private Date time;
    private int status;
    public Body(int status){
        this.setStatus(status);
        this.setTime(new Date());
    }
}