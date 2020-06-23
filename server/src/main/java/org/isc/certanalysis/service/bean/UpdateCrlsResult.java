package org.isc.certanalysis.service.bean;

import java.util.ArrayList;
import java.util.List;

public class UpdateCrlsResult {
    private String schemeName;
    private List<String> exceptions;
    private int allCrls;
    private int updatedCrls = 0;

    public UpdateCrlsResult(String schemeName, int allCrls) {
        this.schemeName = schemeName;
        this.allCrls = allCrls;
    }

    public String getSchemeName() {
        return schemeName;
    }

    public void setSchemeName(String schemeName) {
        this.schemeName = schemeName;
    }

    public int getAllCrls() {
        return allCrls;
    }

    public void setAllCrls(int allCrls) {
        this.allCrls = allCrls;
    }

    public int getUpdatedCrls() {
        return updatedCrls;
    }

    public void setUpdatedCrls(int updatedCrls) {
        this.updatedCrls = updatedCrls;
    }

    public List<String> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<String> exceptions) {
        this.exceptions = exceptions;
    }

    public void increaseUpdatedCrlSum() {
        updatedCrls++;
    }

    public void addException(String exception) {
        if (exceptions == null) {
            exceptions = new ArrayList<>();
        }
        exceptions.add(exception);
    }
}
