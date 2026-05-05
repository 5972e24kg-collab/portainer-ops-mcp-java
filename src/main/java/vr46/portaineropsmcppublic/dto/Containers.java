package vr46.portaineropsmcppublic.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class Containers {
    @JsonProperty("containers")
    public List<ContainerOverview> containers = new ArrayList<>();
}
