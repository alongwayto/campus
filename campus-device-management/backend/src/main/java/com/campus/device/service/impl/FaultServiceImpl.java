package com.campus.device.service.impl;

import com.campus.device.dao.FaultMapper;
import com.campus.device.exception.BusinessException;
import com.campus.device.model.dto.FaultQueryParam;
import com.campus.device.model.dto.PageResult;
import com.campus.device.model.entity.FaultRecord;
import com.campus.device.service.FaultService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FaultServiceImpl implements FaultService {

    private final FaultMapper faultMapper;

    @Override
    public PageResult<FaultRecord> listFaults(FaultQueryParam param) {
        PageHelper.startPage(param.getPageNum(), param.getPageSize());
        List<FaultRecord> faults = faultMapper.selectByCondition(param);
        PageInfo<FaultRecord> pageInfo = new PageInfo<>(faults);
        return new PageResult<>(pageInfo.getTotal(), pageInfo.getList());
    }

    @Override
    public FaultRecord getById(Long id) {
        FaultRecord fault = faultMapper.selectById(id);
        if (fault == null) {
            throw new BusinessException(404, "Fault record not found: " + id);
        }
        return fault;
    }

    @Override
    @Transactional
    public void reportFault(FaultRecord fault) {
        fault.setStatus(0);
        fault.setReportedAt(new Date());
        fault.setCreatedAt(new Date());
        if (fault.getSeverity() == null) {
            fault.setSeverity(1);
        }
        faultMapper.insert(fault);
    }

    @Override
    @Transactional
    public void updateFault(FaultRecord fault) {
        int rows = faultMapper.update(fault);
        if (rows == 0) {
            throw new BusinessException(404, "Fault record not found: " + fault.getId());
        }
    }

    @Override
    @Transactional
    public void deleteFault(Long id) {
        int rows = faultMapper.deleteById(id);
        if (rows == 0) {
            throw new BusinessException(404, "Fault record not found: " + id);
        }
    }

    @Override
    @Transactional
    public void assignFault(Long id, Long assigneeId) {
        FaultRecord fault = getById(id);
        fault.setAssigneeId(assigneeId);
        fault.setStatus(1);
        fault.setAssignedAt(new Date());
        faultMapper.update(fault);
    }

    @Override
    @Transactional
    public void resolveFault(Long id, String notes) {
        FaultRecord fault = getById(id);
        fault.setStatus(3);
        fault.setResolvedAt(new Date());
        fault.setNotes(notes);
        faultMapper.update(fault);
    }
}
