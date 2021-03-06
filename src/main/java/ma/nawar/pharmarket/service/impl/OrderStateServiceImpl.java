package ma.nawar.pharmarket.service.impl;

import ma.nawar.pharmarket.service.OrderStateService;
import ma.nawar.pharmarket.domain.OrderState;
import ma.nawar.pharmarket.repository.OrderStateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * Service Implementation for managing OrderState.
 */
@Service
@Transactional
public class OrderStateServiceImpl implements OrderStateService {

    private final Logger log = LoggerFactory.getLogger(OrderStateServiceImpl.class);

    private final OrderStateRepository orderStateRepository;

    public OrderStateServiceImpl(OrderStateRepository orderStateRepository) {
        this.orderStateRepository = orderStateRepository;
    }

    /**
     * Save a orderState.
     *
     * @param orderState the entity to save
     * @return the persisted entity
     */
    @Override
    public OrderState save(OrderState orderState) {
        log.debug("Request to save OrderState : {}", orderState);
        return orderStateRepository.save(orderState);
    }

    /**
     * Get all the orderStates.
     *
     * @param pageable the pagination information
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public Page<OrderState> findAll(Pageable pageable) {
        log.debug("Request to get all OrderStates");
        return orderStateRepository.findAll(pageable);
    }

    /**
     * Get one orderState by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public OrderState findOne(Long id) {
        log.debug("Request to get OrderState : {}", id);
        return orderStateRepository.findOne(id);
    }

    /**
     * Delete the orderState by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete OrderState : {}", id);
        orderStateRepository.delete(id);
    }
}
