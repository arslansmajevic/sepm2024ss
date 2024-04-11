package at.ac.tuwien.sepr.assignment.individual.rest;

import at.ac.tuwien.sepr.assignment.individual.TestBase;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.horse.HorseSelectionDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentCreateDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentDetailDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentListDto;
import at.ac.tuwien.sepr.assignment.individual.dto.tournament.TournamentStandingsDto;
import at.ac.tuwien.sepr.assignment.individual.type.Sex;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles({"test", "datagen"}) // enable "test" spring profile during test execution in order to pick up configuration from application-test.yml
@SpringBootTest
@EnableWebMvc
@WebAppConfiguration
@DirtiesContext
public class TournamentEndpointTest extends TestBase {
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
  @DirtiesContext
  @Order(2)
  public void gettingAllHorses() throws Exception {
    byte[] body = mockMvc
            .perform(MockMvcRequestBuilders
                    .get("/tournaments")
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

    List<TournamentListDto> tournamentResult = objectMapper.readerFor(TournamentListDto.class)
            .<TournamentListDto>readValues(body).readAll();

    assertThat(tournamentResult).isNotNull();
    assertThat(tournamentResult)
            .hasSize(9)
            .extracting(TournamentListDto::id, TournamentListDto::name, TournamentListDto::startDate, TournamentListDto::endDate)
            .contains(
                    tuple(-1L, "2023 Wien Race", LocalDate.of(2023, 8, 5), LocalDate.of(2023, 11, 5)),
                    tuple(-3L, "2021 Wien 1st Race", LocalDate.of(2021, 1, 1), LocalDate.of(2021, 2, 1)),
                    tuple(-7L, "2016 Tournament Mostar 3rd", LocalDate.of(2016, 5, 1), LocalDate.of(2016, 6, 1)));
  }

  @Test
  @DirtiesContext
  @Order(3)
  public void searchByStartDateBetween2021And2023Returns4Tournaments() throws Exception {
    var body = mockMvc
            .perform(MockMvcRequestBuilders
                    .get("/tournaments")
                    .queryParam("startDate", LocalDate.of(2021, 1, 1).toString())
                    .queryParam("endDate", LocalDate.of(2023, 12, 1).toString())
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();

    List<TournamentListDto> tournamentResult = objectMapper.readerFor(TournamentListDto.class)
            .<TournamentListDto>readValues(body).readAll();

    assertThat(tournamentResult)
            .hasSize(4)
            .extracting(TournamentListDto::id, TournamentListDto::name, TournamentListDto::startDate, TournamentListDto::endDate)
            .contains(
                    tuple(-1L, "2023 Wien Race", LocalDate.of(2023, 8, 5), LocalDate.of(2023, 11, 5)),
                    tuple(-2L, "2022 Sarajevo Race", LocalDate.of(2022, 5, 5), LocalDate.of(2022, 5, 5)),
                    tuple(-3L, "2021 Wien 1st Race", LocalDate.of(2021, 1, 1), LocalDate.of(2021, 2, 1)),
                    tuple(-4L, "2021 Wien 2nd Race", LocalDate.of(2021, 3, 1), LocalDate.of(2021, 4, 1)));
  }

  @Test
  @DirtiesContext
  @Order(4)
  public void createValidTournament() throws Exception {
    var participants = new HorseSelectionDto[]{
        new HorseSelectionDto(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
        new HorseSelectionDto(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
        new HorseSelectionDto(-3L, "Bella", LocalDate.of(2005, 4, 8)),
        new HorseSelectionDto(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
        new HorseSelectionDto(-5L, "Luna", LocalDate.of(2012, 11, 22)),
        new HorseSelectionDto(-6L, "Apollo", LocalDate.of(2003, 9, 3)),
        new HorseSelectionDto(-7L, "Sophie", LocalDate.of(2010, 6, 18)),
        new HorseSelectionDto(-8L, "Max", LocalDate.of(2006, 3, 27))
    };
    TournamentCreateDto requestTournament = new TournamentCreateDto(
            "2023 Test Tournament",
            LocalDate.of(2023, 5, 1),
            LocalDate.of(2023, 6, 21),
            participants);
    var response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/tournaments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestTournament)))
            .andExpect(status().isCreated())
            .andReturn().getResponse();

    var createdTournament = objectMapper.readValue(response.getContentAsString(), TournamentDetailDto.class);

    assertThat(createdTournament).isNotNull();
    assertThat(createdTournament.participants()).hasSize(participants.length);
    for (int i = 0; i < participants.length; i++) {
      assertThat(createdTournament.participants()[participants.length - i - 1].horseId()).isEqualTo(participants[i].id());
      assertThat(createdTournament.participants()[participants.length - i - 1].roundReached()).isEqualTo(1);
    }
  }

  @Test
  @DirtiesContext
  @Order(5)
  public void validityChecksOnCreatingNewTournament() throws Exception {
    var participants = new HorseSelectionDto[]{
        new HorseSelectionDto(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
        new HorseSelectionDto(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
        new HorseSelectionDto(-3L, "Bella", LocalDate.of(2005, 4, 8)),
        new HorseSelectionDto(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
        new HorseSelectionDto(-5L, "Luna", LocalDate.of(2012, 11, 22)),
        new HorseSelectionDto(-6L, "Apollo", LocalDate.of(2003, 9, 3)),
        new HorseSelectionDto(-7L, "Sophie", LocalDate.of(2010, 6, 18)),
        new HorseSelectionDto(-8L, "Max", LocalDate.of(2006, 3, 27))
    };
    TournamentCreateDto noName = new TournamentCreateDto(
            null,
            LocalDate.of(2023, 5, 1),
            LocalDate.of(2023, 6, 21),
            participants);

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/tournaments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(noName)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

    TournamentCreateDto noDate = new TournamentCreateDto(
            "Test",
            null,
            LocalDate.of(2023, 6, 21),
            participants);

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/tournaments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(noDate)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

    TournamentCreateDto conflictingDates = new TournamentCreateDto(
            "Test",
            LocalDate.of(2025, 5, 1),
            LocalDate.of(2023, 6, 21),
            participants);

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/tournaments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(conflictingDates)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

    participants = new HorseSelectionDto[]{
        new HorseSelectionDto(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
        new HorseSelectionDto(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
        new HorseSelectionDto(-3L, "Bella", LocalDate.of(2005, 4, 8)),
        new HorseSelectionDto(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
        new HorseSelectionDto(-5L, "Luna", LocalDate.of(2012, 11, 22)),
        new HorseSelectionDto(-6L, "Apollo", LocalDate.of(2003, 9, 3)),
        new HorseSelectionDto(-7L, "Sophie", LocalDate.of(2010, 6, 18))
    };
    TournamentCreateDto lackingHorses = new TournamentCreateDto(
            "Test",
            LocalDate.of(2025, 5, 1),
            LocalDate.of(2023, 6, 21),
            participants);

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/tournaments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(lackingHorses)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();

    participants = new HorseSelectionDto[]{
        new HorseSelectionDto(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
        new HorseSelectionDto(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
        new HorseSelectionDto(-3L, "Bella", LocalDate.of(2005, 4, 8)),
        new HorseSelectionDto(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
        new HorseSelectionDto(-5L, "Luna", LocalDate.of(2012, 11, 22)),
        new HorseSelectionDto(-6123L, "Apollo", LocalDate.of(2003, 9, 3)),
        new HorseSelectionDto(-210602L, "Sophie", LocalDate.of(2010, 6, 18))
    };

    TournamentCreateDto unknownHorses = new TournamentCreateDto(
            "Test",
            LocalDate.of(2025, 5, 1),
            LocalDate.of(2023, 6, 21),
            participants);

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/tournaments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(unknownHorses)))
            .andExpect(status().isUnprocessableEntity())
            .andReturn().getResponse();
  }

  @Test
  @DirtiesContext
  @Order(6)
  public void generateFirstRoundMatches() throws Exception {
    var participants = new HorseSelectionDto[]{
        new HorseSelectionDto(-1L, "Wendy", LocalDate.of(2019, 8, 5)),
        new HorseSelectionDto(-2L, "Hugo", LocalDate.of(2020, 2, 20)),
        new HorseSelectionDto(-3L, "Bella", LocalDate.of(2005, 4, 8)),
        new HorseSelectionDto(-4L, "Thunder", LocalDate.of(2008, 7, 15)),
        new HorseSelectionDto(-5L, "Luna", LocalDate.of(2012, 11, 22)),
        new HorseSelectionDto(-6L, "Apollo", LocalDate.of(2003, 9, 3)),
        new HorseSelectionDto(-7L, "Sophie", LocalDate.of(2010, 6, 18)),
        new HorseSelectionDto(-8L, "Max", LocalDate.of(2006, 3, 27))
    };
    TournamentCreateDto requestTournament = new TournamentCreateDto(
            "2023 Test Tournament",
            LocalDate.of(2023, 5, 1),
            LocalDate.of(2023, 6, 21),
            participants);
    var response = mockMvc.perform(MockMvcRequestBuilders
                    .post("/tournaments")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(requestTournament)))
            .andExpect(status().isCreated())
            .andReturn().getResponse();

    var createdTournament = objectMapper.readValue(response.getContentAsString(), TournamentDetailDto.class);

    assertThat(createdTournament).isNotNull();

    response = mockMvc.perform(MockMvcRequestBuilders
                    .put("/tournaments/standings/firstRound/" + createdTournament.id())
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andReturn().getResponse();

    var createdStandings = objectMapper.readValue(response.getContentAsString(), TournamentStandingsDto.class);

    assertThat(createdStandings).isNotNull();
    assertThat(createdStandings.tree().thisParticipant()).isNull();
    assertThat(createdStandings.id()).isEqualTo(createdTournament.id());

    assertThat(createdStandings.tree().branches()[0].branches()[0].branches()[0].thisParticipant().horseId()).isEqualTo(-7L);
    assertThat(createdStandings.tree().branches()[0].branches()[0].branches()[1].thisParticipant().horseId()).isEqualTo(-4L);
    assertThat(createdStandings.tree().branches()[0].branches()[1].branches()[0].thisParticipant().horseId()).isEqualTo(-3L);
    assertThat(createdStandings.tree().branches()[0].branches()[1].branches()[1].thisParticipant().horseId()).isEqualTo(-8L);
    assertThat(createdStandings.tree().branches()[1].branches()[0].branches()[0].thisParticipant().horseId()).isEqualTo(-5L);
    assertThat(createdStandings.tree().branches()[1].branches()[0].branches()[1].thisParticipant().horseId()).isEqualTo(-2L);
    assertThat(createdStandings.tree().branches()[1].branches()[1].branches()[0].thisParticipant().horseId()).isEqualTo(-1L);
    assertThat(createdStandings.tree().branches()[1].branches()[1].branches()[1].thisParticipant().horseId()).isEqualTo(-6L);
  }
}
