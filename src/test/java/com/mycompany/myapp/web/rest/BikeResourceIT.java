package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.JhipsterappApp;
import com.mycompany.myapp.domain.Bike;
import com.mycompany.myapp.repository.BikeRepository;
import com.mycompany.myapp.web.rest.errors.ExceptionTranslator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.Validator;

import javax.persistence.EntityManager;
import java.util.List;

import static com.mycompany.myapp.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link BikeResource} REST controller.
 */
@SpringBootTest(classes = JhipsterappApp.class)
public class BikeResourceIT {

    private static final String DEFAULT_MODEL = "AAAAAAAAAA";
    private static final String UPDATED_MODEL = "BBBBBBBBBB";

    private static final String DEFAULT_SERIAL_NO = "AAAAAAAAAA";
    private static final String UPDATED_SERIAL_NO = "BBBBBBBBBB";

    @Autowired
    private BikeRepository bikeRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    @Autowired
    private Validator validator;

    private MockMvc restBikeMockMvc;

    private Bike bike;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final BikeResource bikeResource = new BikeResource(bikeRepository);
        this.restBikeMockMvc = MockMvcBuilders.standaloneSetup(bikeResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter)
            .setValidator(validator).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bike createEntity(EntityManager em) {
        Bike bike = new Bike()
            .model(DEFAULT_MODEL)
            .serialNo(DEFAULT_SERIAL_NO);
        return bike;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Bike createUpdatedEntity(EntityManager em) {
        Bike bike = new Bike()
            .model(UPDATED_MODEL)
            .serialNo(UPDATED_SERIAL_NO);
        return bike;
    }

    @BeforeEach
    public void initTest() {
        bike = createEntity(em);
    }

    @Test
    @Transactional
    public void createBike() throws Exception {
        int databaseSizeBeforeCreate = bikeRepository.findAll().size();

        // Create the Bike
        restBikeMockMvc.perform(post("/api/bikes")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bike)))
            .andExpect(status().isCreated());

        // Validate the Bike in the database
        List<Bike> bikeList = bikeRepository.findAll();
        assertThat(bikeList).hasSize(databaseSizeBeforeCreate + 1);
        Bike testBike = bikeList.get(bikeList.size() - 1);
        assertThat(testBike.getModel()).isEqualTo(DEFAULT_MODEL);
        assertThat(testBike.getSerialNo()).isEqualTo(DEFAULT_SERIAL_NO);
    }

    @Test
    @Transactional
    public void createBikeWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = bikeRepository.findAll().size();

        // Create the Bike with an existing ID
        bike.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restBikeMockMvc.perform(post("/api/bikes")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bike)))
            .andExpect(status().isBadRequest());

        // Validate the Bike in the database
        List<Bike> bikeList = bikeRepository.findAll();
        assertThat(bikeList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllBikes() throws Exception {
        // Initialize the database
        bikeRepository.saveAndFlush(bike);

        // Get all the bikeList
        restBikeMockMvc.perform(get("/api/bikes?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bike.getId().intValue())))
            .andExpect(jsonPath("$.[*].model").value(hasItem(DEFAULT_MODEL)))
            .andExpect(jsonPath("$.[*].serialNo").value(hasItem(DEFAULT_SERIAL_NO)));
    }
    
    @Test
    @Transactional
    public void getBike() throws Exception {
        // Initialize the database
        bikeRepository.saveAndFlush(bike);

        // Get the bike
        restBikeMockMvc.perform(get("/api/bikes/{id}", bike.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bike.getId().intValue()))
            .andExpect(jsonPath("$.model").value(DEFAULT_MODEL))
            .andExpect(jsonPath("$.serialNo").value(DEFAULT_SERIAL_NO));
    }

    @Test
    @Transactional
    public void getNonExistingBike() throws Exception {
        // Get the bike
        restBikeMockMvc.perform(get("/api/bikes/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateBike() throws Exception {
        // Initialize the database
        bikeRepository.saveAndFlush(bike);

        int databaseSizeBeforeUpdate = bikeRepository.findAll().size();

        // Update the bike
        Bike updatedBike = bikeRepository.findById(bike.getId()).get();
        // Disconnect from session so that the updates on updatedBike are not directly saved in db
        em.detach(updatedBike);
        updatedBike
            .model(UPDATED_MODEL)
            .serialNo(UPDATED_SERIAL_NO);

        restBikeMockMvc.perform(put("/api/bikes")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedBike)))
            .andExpect(status().isOk());

        // Validate the Bike in the database
        List<Bike> bikeList = bikeRepository.findAll();
        assertThat(bikeList).hasSize(databaseSizeBeforeUpdate);
        Bike testBike = bikeList.get(bikeList.size() - 1);
        assertThat(testBike.getModel()).isEqualTo(UPDATED_MODEL);
        assertThat(testBike.getSerialNo()).isEqualTo(UPDATED_SERIAL_NO);
    }

    @Test
    @Transactional
    public void updateNonExistingBike() throws Exception {
        int databaseSizeBeforeUpdate = bikeRepository.findAll().size();

        // Create the Bike

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBikeMockMvc.perform(put("/api/bikes")
            .contentType(TestUtil.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(bike)))
            .andExpect(status().isBadRequest());

        // Validate the Bike in the database
        List<Bike> bikeList = bikeRepository.findAll();
        assertThat(bikeList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteBike() throws Exception {
        // Initialize the database
        bikeRepository.saveAndFlush(bike);

        int databaseSizeBeforeDelete = bikeRepository.findAll().size();

        // Delete the bike
        restBikeMockMvc.perform(delete("/api/bikes/{id}", bike.getId())
            .accept(TestUtil.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Bike> bikeList = bikeRepository.findAll();
        assertThat(bikeList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
