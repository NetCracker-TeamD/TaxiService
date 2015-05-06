package com.teamd.taxi.models.admin;

/**
 * Created on 06-May-15.
 *
 * @author Nazar Dub
 */
public class AdminResponseModel<T> {
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_FAILURE = "failure";

    private String result;
    private T content;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
