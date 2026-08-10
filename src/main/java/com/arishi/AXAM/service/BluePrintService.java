package com.arishi.AXAM.service;

import com.arishi.AXAM.dto.request.BlueprintRequest;
import com.arishi.AXAM.dto.responce.BluePrintResponse;

import java.util.List;

public interface BluePrintService {

    BluePrintResponse createBlueprint(BlueprintRequest request);

    List<BluePrintResponse> getAllBlueprints();

    BluePrintResponse getBlueprintByTitle(String title);
}
