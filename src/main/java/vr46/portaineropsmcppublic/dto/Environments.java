package vr46.portaineropsmcppublic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Environments {
    @JsonProperty("environments")
    public List<EnvironmentOverview> environments = new ArrayList<>();
}
