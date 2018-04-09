package ma.nawar.pharmarket.web.rest;

import ma.nawar.pharmarket.PharmarketApp;

import ma.nawar.pharmarket.domain.RuleType;
import ma.nawar.pharmarket.repository.RuleTypeRepository;
import ma.nawar.pharmarket.service.RuleTypeService;
import ma.nawar.pharmarket.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static ma.nawar.pharmarket.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the RuleTypeResource REST controller.
 *
 * @see RuleTypeResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PharmarketApp.class)
public class RuleTypeResourceIntTest {

    private static final String DEFAULT_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CODE = "BBBBBBBBBB";

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private RuleTypeRepository ruleTypeRepository;

    @Autowired
    private RuleTypeService ruleTypeService;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restRuleTypeMockMvc;

    private RuleType ruleType;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final RuleTypeResource ruleTypeResource = new RuleTypeResource(ruleTypeService);
        this.restRuleTypeMockMvc = MockMvcBuilders.standaloneSetup(ruleTypeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static RuleType createEntity(EntityManager em) {
        RuleType ruleType = new RuleType()
            .code(DEFAULT_CODE)
            .name(DEFAULT_NAME);
        return ruleType;
    }

    @Before
    public void initTest() {
        ruleType = createEntity(em);
    }

    @Test
    @Transactional
    public void createRuleType() throws Exception {
        int databaseSizeBeforeCreate = ruleTypeRepository.findAll().size();

        // Create the RuleType
        restRuleTypeMockMvc.perform(post("/api/rule-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ruleType)))
            .andExpect(status().isCreated());

        // Validate the RuleType in the database
        List<RuleType> ruleTypeList = ruleTypeRepository.findAll();
        assertThat(ruleTypeList).hasSize(databaseSizeBeforeCreate + 1);
        RuleType testRuleType = ruleTypeList.get(ruleTypeList.size() - 1);
        assertThat(testRuleType.getCode()).isEqualTo(DEFAULT_CODE);
        assertThat(testRuleType.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createRuleTypeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = ruleTypeRepository.findAll().size();

        // Create the RuleType with an existing ID
        ruleType.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restRuleTypeMockMvc.perform(post("/api/rule-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ruleType)))
            .andExpect(status().isBadRequest());

        // Validate the RuleType in the database
        List<RuleType> ruleTypeList = ruleTypeRepository.findAll();
        assertThat(ruleTypeList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void checkCodeIsRequired() throws Exception {
        int databaseSizeBeforeTest = ruleTypeRepository.findAll().size();
        // set the field null
        ruleType.setCode(null);

        // Create the RuleType, which fails.

        restRuleTypeMockMvc.perform(post("/api/rule-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ruleType)))
            .andExpect(status().isBadRequest());

        List<RuleType> ruleTypeList = ruleTypeRepository.findAll();
        assertThat(ruleTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void checkNameIsRequired() throws Exception {
        int databaseSizeBeforeTest = ruleTypeRepository.findAll().size();
        // set the field null
        ruleType.setName(null);

        // Create the RuleType, which fails.

        restRuleTypeMockMvc.perform(post("/api/rule-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ruleType)))
            .andExpect(status().isBadRequest());

        List<RuleType> ruleTypeList = ruleTypeRepository.findAll();
        assertThat(ruleTypeList).hasSize(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    public void getAllRuleTypes() throws Exception {
        // Initialize the database
        ruleTypeRepository.saveAndFlush(ruleType);

        // Get all the ruleTypeList
        restRuleTypeMockMvc.perform(get("/api/rule-types?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(ruleType.getId().intValue())))
            .andExpect(jsonPath("$.[*].code").value(hasItem(DEFAULT_CODE.toString())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME.toString())));
    }

    @Test
    @Transactional
    public void getRuleType() throws Exception {
        // Initialize the database
        ruleTypeRepository.saveAndFlush(ruleType);

        // Get the ruleType
        restRuleTypeMockMvc.perform(get("/api/rule-types/{id}", ruleType.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(ruleType.getId().intValue()))
            .andExpect(jsonPath("$.code").value(DEFAULT_CODE.toString()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingRuleType() throws Exception {
        // Get the ruleType
        restRuleTypeMockMvc.perform(get("/api/rule-types/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateRuleType() throws Exception {
        // Initialize the database
        ruleTypeService.save(ruleType);

        int databaseSizeBeforeUpdate = ruleTypeRepository.findAll().size();

        // Update the ruleType
        RuleType updatedRuleType = ruleTypeRepository.findOne(ruleType.getId());
        // Disconnect from session so that the updates on updatedRuleType are not directly saved in db
        em.detach(updatedRuleType);
        updatedRuleType
            .code(UPDATED_CODE)
            .name(UPDATED_NAME);

        restRuleTypeMockMvc.perform(put("/api/rule-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(updatedRuleType)))
            .andExpect(status().isOk());

        // Validate the RuleType in the database
        List<RuleType> ruleTypeList = ruleTypeRepository.findAll();
        assertThat(ruleTypeList).hasSize(databaseSizeBeforeUpdate);
        RuleType testRuleType = ruleTypeList.get(ruleTypeList.size() - 1);
        assertThat(testRuleType.getCode()).isEqualTo(UPDATED_CODE);
        assertThat(testRuleType.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingRuleType() throws Exception {
        int databaseSizeBeforeUpdate = ruleTypeRepository.findAll().size();

        // Create the RuleType

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restRuleTypeMockMvc.perform(put("/api/rule-types")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(ruleType)))
            .andExpect(status().isCreated());

        // Validate the RuleType in the database
        List<RuleType> ruleTypeList = ruleTypeRepository.findAll();
        assertThat(ruleTypeList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteRuleType() throws Exception {
        // Initialize the database
        ruleTypeService.save(ruleType);

        int databaseSizeBeforeDelete = ruleTypeRepository.findAll().size();

        // Get the ruleType
        restRuleTypeMockMvc.perform(delete("/api/rule-types/{id}", ruleType.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate the database is empty
        List<RuleType> ruleTypeList = ruleTypeRepository.findAll();
        assertThat(ruleTypeList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(RuleType.class);
        RuleType ruleType1 = new RuleType();
        ruleType1.setId(1L);
        RuleType ruleType2 = new RuleType();
        ruleType2.setId(ruleType1.getId());
        assertThat(ruleType1).isEqualTo(ruleType2);
        ruleType2.setId(2L);
        assertThat(ruleType1).isNotEqualTo(ruleType2);
        ruleType1.setId(null);
        assertThat(ruleType1).isNotEqualTo(ruleType2);
    }
}
