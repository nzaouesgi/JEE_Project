package fr.esgi.secureupload.common.infrastructure.controllers.response;

@lombok.Getter
@lombok.Setter
public class DataBody<T> extends Body {
    private T data;
    public DataBody(T data, int status){
        super(status);
        this.data = data;
    }
}
