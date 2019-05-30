package com.justinaspetuchovas.health.service;

import com.justinaspetuchovas.health.pagination.PageCommand;
import com.justinaspetuchovas.health.pagination.SliceDto;
import com.justinaspetuchovas.health.repository.PurchaseFactRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * Service used to perform operations with purchase facts.
 */
@Service
public class PurchaseFactService {
  private static final Logger logger = LogManager.getLogger(PurchaseFactService.class);
  private final PurchaseFactRepository purchaseFactRepository;

  @Autowired
  public PurchaseFactService(PurchaseFactRepository purchaseFactRepository) {
    this.purchaseFactRepository = purchaseFactRepository;
  }

  @Transactional
  public SliceDto getPurchaseFacts(UUID medicalPrescriptionId, PageCommand pageCommand) {
    int pageNumber = pageCommand.getPageNumber();
    int pageSize = pageCommand.getPageSize();

    Slice<Date> purchaseFactsSlice = purchaseFactRepository.getPurchaseFacts(
        medicalPrescriptionId,
        new PageRequest(
            pageNumber,
            pageSize,
            Sort.Direction.DESC,
            "purchaseDate",
            "id"
        )
    );

    logger.info(
        "Returned a page number {} of size {} with a list of medical prescription's with id \"{}\" "
            + "purchase facts.",
        pageNumber,
        pageSize,
        medicalPrescriptionId
    );

    return new SliceDto(purchaseFactsSlice.getContent(), purchaseFactsSlice.hasNext());
  }
}
