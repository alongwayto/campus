package com.campus.device.service;

import com.campus.device.model.dto.FaultQueryParam;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.FaultRecord;

public interface FaultService {

    PageResult<FaultRecord> listFaults(FaultQueryParam param);

    FaultRecord getById(Long id);

    void reportFault(FaultRecord fault);

    void updateFault(FaultRecord fault);

    void deleteFault(Long id);

    void assignFault(Long id, Long assigneeId);

    void resolveFault(Long id, String notes);
}
