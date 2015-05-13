package com.teamd.taxi.models.admin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created on 06-May-15.
 *
 * @author Nazar Dub
 */
public class AdminResponseModel<T> {
    public static final String RESULT_SUCCESS = "success";
    public static final String RESULT_FAILURE = "failure";

    private String result = RESULT_FAILURE;
    private T content;

    public AdminResponseModel() {
    }

    public AdminResponseModel(T content) {
        this.content = content;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setResultSuccess() {
        this.result = RESULT_SUCCESS;
    }

    public void setResultFailure() {
        this.result = RESULT_FAILURE;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("result", this.result);
        map.put("content", this.content);
        return map;
    }
}
