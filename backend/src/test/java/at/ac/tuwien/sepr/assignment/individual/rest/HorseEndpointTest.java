package at.ac.tuwien.sepr.assignment.individual.rest;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.breed.BreedDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
@DirtiesContext
public class HorseEndpointTest extends TestBase {

  @Autowired
  private WebApplicationContext webAppContext;
  private MockMvc mockMvc;

  @Autowired
  ObjectMapper objectMapper;

  @BeforeEach
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(webAppContext).build();
  }

  @Test
  @Order(1)
  public void gettingNonexistentUrlReturns404() throws Exception {
    mockMvc
        .perform(MockMvcRequestBuilders
            .get("/asdf123")
        ).andExpect(status().isNotFound());
  }

  @Test
  @Order(2)
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    List<HorseListDto> horseResult = objectMapper.readerFor(HorseListDto.class)
        .<HorseListDto>readValues(body).readAll();

    assertThat(horseResult).isNotNull();
    assertThat(horseResult)
        .hasSize(32)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth)
        .contains(
            tuple(-1L, "Wendy", Sex.FEMALE, LocalDate.of(2019, 8, 5)),
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10)),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6)),
            tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20)));
  }

  @Test
  @Order(3)
  public void searchByBreedWelFindsThreeHorses() throws Exception {
    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .queryParam("breed", "Wel")
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    var horsesIterator = objectMapper.readerFor(HorseListDto.class)
        .<HorseListDto>readValues(body);
    assertNotNull(horsesIterator);
    var horses = new ArrayList<HorseListDto>();
    horsesIterator.forEachRemaining(horses::add);
    // We don't have height and weight of the horses here, so no reason to test for them.
    assertThat(horses)
        .extracting("id", "name", "sex", "dateOfBirth", "breed.name")
        .as("ID, Name, Sex, Date of Birth, Breed Name")
        .containsExactlyInAnyOrder(
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10), "Welsh Cob"),
            tuple(-21L, "Bella", Sex.FEMALE, LocalDate.of(2003, 7, 6), "Welsh Cob"),
            tuple(-2L, "Hugo", Sex.MALE, LocalDate.of(2020, 2, 20), "Welsh Pony")
        );
  }

  @Test
  @Order(4)
  public void searchByBirthDateBetween2017And2018ReturnsFourHorses() throws Exception {
    var body = mockMvc
        .perform(MockMvcRequestBuilders
            .get("/horses")
            .queryParam("bornEarliest", LocalDate.of(2017, 3, 5).toString())
            .queryParam("bornLatest", LocalDate.of(2018, 10, 10).toString())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsByteArray();

    var horsesResult = objectMapper.readerFor(HorseListDto.class)
        .<HorseListDto>readValues(body);
    assertNotNull(horsesResult);

    var horses = new ArrayList<HorseListDto>();
    horsesResult.forEachRemaining(horses::add);

    assertThat(horses)
        .hasSize(4)
        .extracting(HorseListDto::id, HorseListDto::name, HorseListDto::sex, HorseListDto::dateOfBirth, (h) -> h.breed().name())
        .containsExactlyInAnyOrder(
            tuple(-24L, "Rocky", Sex.MALE, LocalDate.of(2018, 8, 19),
                "Dartmoor Pony"),
            tuple(-26L, "Daisy", Sex.FEMALE, LocalDate.of(2017, 12, 1),
                "Hanoverian"),
            tuple(-31L, "Leo", Sex.MALE, LocalDate.of(2017, 3, 5),
                "Haflinger"),
            tuple(-32L, "Luna", Sex.FEMALE, LocalDate.of(2018, 10, 10),
                "Welsh Cob"));
  }

  @Test
  @Order(5)
  public void getHorseByIdSucessful() throws Exception {
    var response = mockMvc.perform(MockMvcRequestBuilders
                    .get("/horses/-32")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse();

    var createdHorse = objectMapper.readValue(response.getContentAsString(), HorseDetailDto.class);

    assertNotNull(createdHorse);

    assertThat(createdHorse.name()).isEqualTo("Luna");
    assertThat(createdHorse.sex()).isEqualTo(Sex.FEMALE);
    assertThat(createdHorse.dateOfBirth()).isEqualTo(LocalDate.of(2018, 10, 10));
    assertThat(createdHorse.breed()).isEqualTo(new BreedDto(-19, "Welsh Cob"));
  }

  @Test
  @Order(6)
  public void getNotPresentHorseById() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
                    .get("/horses/-210602")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn().getResponse();
  }

  @Test
  @DirtiesContext
  @Order(7)
  public void createNewValidHorse() throws Exception {
    HorseCreateDto requestHorse = new HorseCreateDto(
            "Completely Valid Horse",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    var response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestHorse)))
            .andExpect(status().isCreated())
            .andReturn().getResponse();

    var createdHorse = objectMapper.readValue(response.getContentAsString(), HorseDetailDto.class);
    assertNotNull(createdHorse);

    assertThat(createdHorse.name()).isEqualTo("Completely Valid Horse");
    assertThat(createdHorse.height()).isEqualTo(2.3f);
    assertThat(createdHorse.weight()).isEqualTo(300);
    assertNotNull(createdHorse.breed());
    assertThat(createdHorse.breed()).isEqualTo(new BreedDto(-19, "Welsh Cob"));
  }

  @Test
  @DirtiesContext
  @Order(8)
  public void createNewHorseBreedAsOptional() throws Exception {
    HorseCreateDto requestHorse = new HorseCreateDto(
            "Completely Valid Horse",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            null
    );

    var response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestHorse)))
            .andExpect(status().isCreated())
            .andReturn().getResponse();

    var createdHorse = objectMapper.readValue(response.getContentAsString(), HorseDetailDto.class);
    assertNotNull(createdHorse);

    assertThat(createdHorse.name()).isEqualTo("Completely Valid Horse");
    assertThat(createdHorse.height()).isEqualTo(2.3f);
    assertThat(createdHorse.weight()).isEqualTo(300);
    assertThat(createdHorse.breed()).isEqualTo(null);
  }

  @Test
  @Order(9)
  public void validityCheckOnCreatingOfNewHorse() throws Exception {
    HorseCreateDto missingName = new HorseCreateDto(
            null,
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    var response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(missingName)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse name was not defined"));

    HorseCreateDto sexMissing = new HorseCreateDto(
            "Test",
            null,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sexMissing)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse sex was not defined"));

    HorseCreateDto dateOfBirthMissing = new HorseCreateDto(
            "Test",
            Sex.FEMALE,
            null,
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dateOfBirthMissing)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse date of birth is not defined"));

    HorseCreateDto heightAndWeightMissing = new HorseCreateDto(
            "Test",
            Sex.FEMALE,
            LocalDate.of(2002, 6, 21),
            0, // hence these are long, it will always be picked up as 0
            0,
            new BreedDto(-19, "Welsh Cob")
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(heightAndWeightMissing)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse height can not be 0 / is not defined"));
    assert (response.getContentAsString().contains("Horse weight can not be 0 / is not defined"));

    HorseCreateDto parametersNotGiven = new HorseCreateDto(
            null,
            null,
            null,
            0, // hence these are long, it will always be picked up as 0
            0,
            null
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(parametersNotGiven)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse sex was not defined"));
    assert (response.getContentAsString().contains("Horse date of birth is not defined"));
    assert (response.getContentAsString().contains("Horse height can not be 0 / is not defined"));
    assert (response.getContentAsString().contains("Horse weight can not be 0 / is not defined"));

    HorseCreateDto negativeValues = new HorseCreateDto(
            "Arslan",
            Sex.FEMALE,
            LocalDate.of(2002, 6, 21),
            -2, // hence these are long, it will always be picked up as 0
            -3,
            null
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(negativeValues)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Weight can not be negative"));

    HorseCreateDto unknownBreedId = new HorseCreateDto(
            "Completely Valid Horse",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(1024, "False")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(unknownBreedId)))
            .andExpect(status().isConflict())
            .andReturn().getResponse();

    HorseCreateDto dateOfBirthAheadOfToday = new HorseCreateDto(
            "Completely Valid Horse",
            Sex.MALE,
            LocalDate.now().plusDays(5),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dateOfBirthAheadOfToday)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse date of birth can not be set in future"));
  }

  @Test
  @DirtiesContext
  @Order(10)
  public void deleteHorseSuccessful() throws Exception {
    var response = mockMvc.perform(MockMvcRequestBuilders
                    .delete("/horses/-12")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse();

    var createdHorse = objectMapper.readValue(response.getContentAsString(), HorseDetailDto.class);
    assertNotNull(createdHorse);
    System.out.println(createdHorse);

    assertThat(createdHorse.id()).isEqualTo(-12L);
    assertThat(createdHorse.name()).isEqualTo("Charlie");
    assertThat(createdHorse.height()).isEqualTo(1.68f);
    assertThat(createdHorse.weight()).isEqualTo(680);
    assertThat(createdHorse.breed()).isEqualTo(new BreedDto(-10, "Icelandic Horse"));

    mockMvc.perform(MockMvcRequestBuilders
                    .delete("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn().getResponse();
  }

  @Test
  @Order(11)
  public void deleteOnHorseThatIsNotPresent() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
                    .delete("/horses/-999")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound())
            .andReturn().getResponse();
  }

  @Test
  @Order(12)
  public void deleteHorseThatIsPresentOnTournament() throws Exception {
    mockMvc.perform(MockMvcRequestBuilders
                    .delete("/horses/-1")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
  }

  @Test
  @DirtiesContext
  @Order(13)
  public void updateHorseSuccessful() throws Exception {
    HorseDetailDto requestHorse = new HorseDetailDto(
            -12L,
            "Charlie Changed",
            Sex.MALE,
            LocalDate.of(2012, 2, 11),
            2.34f,
            250.6f,
            new BreedDto(-19, "Welsh Cob")
    );

    var response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestHorse)))
            .andExpect(status().isOk())
            .andReturn().getResponse();

    var createdHorse = objectMapper.readValue(response.getContentAsString(), HorseDetailDto.class);
    assertNotNull(createdHorse);

    assert (createdHorse.name().equals("Charlie Changed"));
    assert (createdHorse.height() == 2.34f);
  }

  @Test
  @Order(14)
  public void updateHorseDoesNotExist() throws Exception {
    HorseDetailDto requestHorse = new HorseDetailDto(
            -12L,
            "Charlie Changed",
            Sex.MALE,
            LocalDate.of(2012, 2, 11),
            2.34f,
            250.6f,
            new BreedDto(-19, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-210602")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestHorse)))
            .andExpect(status().isNotFound())
            .andReturn().getResponse();
  }

  @Test
  @Order(15)
  public void updateHorseValidityCheck() throws Exception {

    HorseDetailDto missingName = new HorseDetailDto(
            null,
            null,
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    var response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(missingName)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse name was not defined"));

    HorseDetailDto sexMissing = new HorseDetailDto(
            null,
            "Test",
            null,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(sexMissing)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse sex was not defined"));

    HorseDetailDto dateOfBirthMissing = new HorseDetailDto(
            null,
            "Test",
            Sex.FEMALE,
            null,
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dateOfBirthMissing)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse date of birth is not defined"));

    HorseDetailDto heightAndWeightMissing = new HorseDetailDto(
            null,
            "Test",
            Sex.FEMALE,
            LocalDate.of(2002, 6, 21),
            0, // hence these are long, it will always be picked up as 0
            0,
            new BreedDto(-19, "Welsh Cob")
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(heightAndWeightMissing)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse height can not be 0 / is not defined"));
    assert (response.getContentAsString().contains("Horse weight can not be 0 / is not defined"));

    HorseDetailDto parametersNotGiven = new HorseDetailDto(
            null,
            null,
            null,
            null,
            0, // hence these are long, it will always be picked up as 0
            0,
            null
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(parametersNotGiven)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse sex was not defined"));
    assert (response.getContentAsString().contains("Horse date of birth is not defined"));
    assert (response.getContentAsString().contains("Horse height can not be 0 / is not defined"));
    assert (response.getContentAsString().contains("Horse weight can not be 0 / is not defined"));

    HorseDetailDto negativeValues = new HorseDetailDto(
            null,
            "Arslan",
            Sex.FEMALE,
            LocalDate.of(2002, 6, 21),
            -2, // hence these are long, it will always be picked up as 0
            -3,
            null
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(negativeValues)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Weight can not be negative"));

    HorseDetailDto unknownBreedId = new HorseDetailDto(
            null,
            "Completely Valid Horse",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(1024, "False")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(unknownBreedId)))
            .andExpect(status().isConflict())
            .andReturn().getResponse();

    HorseDetailDto dateOfBirthAheadOfToday = new HorseDetailDto(
            null,
            "Completely Valid Horse",
            Sex.MALE,
            LocalDate.now().plusDays(5),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(dateOfBirthAheadOfToday)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
    assert (response.getContentAsString().contains("Validation of horse for create failed"));
    assert (response.getContentAsString().contains("Horse date of birth can not be set in future"));
  }

  @Test
  @Order(16)
  public void updateHorseWithFalseBreed() throws Exception {

    HorseDetailDto missingName = new HorseDetailDto(
            null,
            "arslan",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-210602, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(missingName)))
            .andExpect(status().isConflict())
            .andReturn().getResponse();
  }

  @Test
  @Order(17)
  public void updateValidHorseButUnknownId() throws Exception {

    HorseDetailDto validHorse = new HorseDetailDto(
            null,
            "Valid Horse",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-210602")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(validHorse)))
            .andExpect(status().isNotFound())
            .andReturn().getResponse();
  }

  @Test
  @Order(18)
  public void updateAHorseConflictingDatabaseRestrictions() throws Exception {

    HorseDetailDto nameTooLong = new HorseDetailDto(
            null,
            "a".repeat(500),
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(nameTooLong)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

    HorseDetailDto heightTooBig = new HorseDetailDto(
            null,
            "a",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2000.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(heightTooBig)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

    HorseDetailDto weightTooBig = new HorseDetailDto(
            null,
            "a",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            110000,
            new BreedDto(-19, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .put("/horses/-12")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(weightTooBig)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
  }

  @Test
  @Order(19)
  public void createAHorseConflictingDatabaseRestrictions() throws Exception {
    HorseCreateDto nameTooLong = new HorseCreateDto(
            "Completely Valid Horse".repeat(300),
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(nameTooLong)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

    HorseCreateDto heightTooBig = new HorseCreateDto(
            "Completely Valid Horse",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2000.3f,
            300,
            new BreedDto(-19, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(heightTooBig)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

    HorseCreateDto weightTooBig = new HorseCreateDto(
            "Completely Valid Horse",
            Sex.MALE,
            LocalDate.of(2002, 6, 21),
            2.3f,
            110000,
            new BreedDto(-19, "Welsh Cob")
    );

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/horses")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(weightTooBig)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

  }
}
