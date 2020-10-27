package ru.resql.batch;

import com.google.common.base.Functions;
import com.zaxxer.hikari.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import ru.resql.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class BatchTest {
	private final PostgresqlDbPipe pipe;

	/**
	 * List of "Game Of Thrones" main characters. Appearance by seasons. Main cast.
	 * See https://en.wikipedia.org/wiki/List_of_Game_of_Thrones_characters#Main_cast
	 */
	private static final Object[][][] gameOfThronesSeasons = new Object[][][] {{
		// Season 1
		{1, "Ned Stark", "Main"}, {2, "Robert Baratheon", "Main"}, {3, "Jaime Lannister", "Main"},
		{4, "Catelyn Stark", "Main"}, {5, "Cersei Lannister", "Main"}, {6, "Daenerys Targaryen", "Main"},
		{7, "Jorah Mormont", "Main"}, {8, "Viserys Targaryen", "Main"}, {9, "Jon Snow", "Main"},
		{10, "Robb Stark", "Main"}, {11, "Sansa Stark", "Main"}, {12, "Arya Stark", "Main"},
		{13, "Theon Greyjoy", "Main"}, {14, "Bran Stark", "Main"}, {15, "Joffrey Baratheon", "Main"},
		{16, "Sandor \"The Hound\" Clegane", "Main"}, {17, "Tyrion Lannister", "Main"}, {18, "Petyr Baelish", "Main"},
		{19, "Samwell Tarly", "Recurring"}, {20, "Jeor Mormont", "Recurring"}, {21, "Bronn", "Recurring"},
		{22, "Varys", "Recurring"}, {23, "Shae", "Guest"}, {24, "Tywin Lannister", "Recurring"},
		{25, "Gendry", "Guest"}, {26, "Tommen Baratheon", "Recurring"}
	}, {
		// Season 2
		{3, "Jaime Lannister", "Main"}, {4, "Catelyn Stark", "Main"}, {5, "Cersei Lannister", "Main"},
		{6, "Daenerys Targaryen", "Main"}, {7, "Jorah Mormont", "Main"}, {9, "Jon Snow", "Main"},
		{10, "Robb Stark", "Main"}, {11, "Sansa Stark", "Main"}, {12, "Arya Stark", "Main"},
		{13, "Theon Greyjoy", "Main"}, {14, "Bran Stark", "Main"}, {15, "Joffrey Baratheon", "Main"},
		{16, "Sandor \"The Hound\" Clegane", "Main"}, {17, "Tyrion Lannister", "Main"}, {18, "Petyr Baelish", "Main"},
		{27, "Davos Seaworth", "Main"}, {19, "Samwell Tarly", "Main"}, {28, "Stannis Baratheon", "Main"},
		{29, "Melisandre", "Main"}, {20, "Jeor Mormont", "Main"}, {21, "Bronn", "Main"},
		{22, "Varys", "Main"}, {23, "Shae", "Main"}, {30, "Margaery Tyrell", "Main"},
		{24, "Tywin Lannister", "Main"}, {31, "Talisa Maegyr", "Recurring"}, {32, "Ygritte", "Recurring"},
		{25, "Gendry", "Recurring"}, {33, "Brienne of Tarth", "Recurring"}, {34, "Gilly", "Recurring"},
		{26, "Tommen Baratheon", "Recurring"}, {35, "Jaqen H'ghar", "Recurring"}, {36, "Roose Bolton", "Recurring"}
	}, {
		// Season 3
		{3, "Jaime Lannister", "Main"}, {4, "Catelyn Stark", "Main"}, {5, "Cersei Lannister", "Main"},
		{6, "Daenerys Targaryen", "Main"}, {7, "Jorah Mormont", "Main"}, {9, "Jon Snow", "Main"},
		{10, "Robb Stark", "Main"}, {11, "Sansa Stark", "Main"}, {12, "Arya Stark", "Main"},
		{13, "Theon Greyjoy", "Main"}, {14, "Bran Stark", "Main"}, {15, "Joffrey Baratheon", "Main"},
		{16, "Sandor \"The Hound\" Clegane", "Main"}, {17, "Tyrion Lannister", "Main"}, {18, "Petyr Baelish", "Main"},
		{27, "Davos Seaworth", "Main"}, {19, "Samwell Tarly", "Main"}, {28, "Stannis Baratheon", "Main"},
		{29, "Melisandre", "Main"}, {20, "Jeor Mormont", "Main"}, {21, "Bronn", "Main"},
		{22, "Varys", "Main"}, {23, "Shae", "Main"}, {30, "Margaery Tyrell", "Main"},
		{24, "Tywin Lannister", "Main"}, {31, "Talisa Maegyr", "Main"}, {32, "Ygritte", "Main"},
		{25, "Gendry", "Main"}, {37, "Tormund Giantsbane", "Recurring"}, {33, "Brienne of Tarth", "Recurring"},
		{38, "Ramsay Bolton", "Recurring"}, {34, "Gilly", "Recurring"}, {39, "Daario Naharis", "Recurring"},
		{40, "Missandei", "Recurring"}, {36, "Roose Bolton", "Recurring"}, {41, "Grey Worm", "Recurring"}
	}, {
		// Season 4
		{3, "Jaime Lannister", "Main"}, {5, "Cersei Lannister", "Main"}, {6, "Daenerys Targaryen", "Main"},
		{7, "Jorah Mormont", "Main"}, {9, "Jon Snow", "Main"}, {11, "Sansa Stark", "Main"},
		{12, "Arya Stark", "Main"}, {13, "Theon Greyjoy", "Main"}, {14, "Bran Stark", "Main"},
		{15, "Joffrey Baratheon", "Main"}, {16, "Sandor \"The Hound\" Clegane", "Main"}, {17, "Tyrion Lannister", "Main"},
		{18, "Petyr Baelish", "Main"}, {27, "Davos Seaworth", "Main"}, {19, "Samwell Tarly", "Main"},
		{28, "Stannis Baratheon", "Main"}, {29, "Melisandre", "Main"}, {21, "Bronn", "Main"},
		{22, "Varys", "Main"}, {23, "Shae", "Main"}, {30, "Margaery Tyrell", "Main"},
		{24, "Tywin Lannister", "Main"}, {32, "Ygritte", "Main"}, {37, "Tormund Giantsbane", "Main"},
		{33, "Brienne of Tarth", "Main"}, {38, "Ramsay Bolton", "Main"}, {34, "Gilly", "Main"},
		{39, "Daario Naharis", "Recurring"}, {40, "Missandei", "Recurring"}, {42, "Tommen Baratheon", "Recurring"},
		{43, "Ellaria Sand", "Recurring"}, {36, "Roose Bolton", "Guest"}, {41, "Grey Worm", "Recurring"}
	}, {
		// Season 5
		{3, "Jaime Lannister", "Main"}, {5, "Cersei Lannister", "Main"}, {6, "Daenerys Targaryen", "Main"},
		{7, "Jorah Mormont", "Main"}, {9, "Jon Snow", "Main"}, {11, "Sansa Stark", "Main"},
		{12, "Arya Stark", "Main"}, {13, "Theon Greyjoy", "Main"}, {17, "Tyrion Lannister", "Main"},
		{18, "Petyr Baelish", "Main"}, {27, "Davos Seaworth", "Main"}, {19, "Samwell Tarly", "Main"},
		{28, "Stannis Baratheon", "Main"}, {29, "Melisandre", "Main"}, {21, "Bronn", "Main"},
		{22, "Varys", "Main"}, {30, "Margaery Tyrell", "Main"}, {24, "Tywin Lannister", "Main"},
		{37, "Tormund Giantsbane", "Main"}, {33, "Brienne of Tarth", "Main"}, {38, "Ramsay Bolton", "Main"},
		{34, "Gilly", "Main"}, {39, "Daario Naharis", "Main"}, {40, "Missandei", "Main"},
		{42, "Tommen Baratheon", "Main"}, {43, "Ellaria Sand", "Main"}, {44, "Jaqen H'ghar", "Main"},
		{36, "Roose Bolton", "Main"}, {44, "The High Sparrow", "Recurring"}, {41, "Grey Worm", "Recurring"}
	}, {
		// Season 6
		{45, "Ned Stark", "Recurring"}, {3, "Jaime Lannister", "Main"}, {5, "Cersei Lannister", "Main"},
		{6, "Daenerys Targaryen", "Main"}, {7, "Jorah Mormont", "Main"}, {9, "Jon Snow", "Main"},
		{11, "Sansa Stark", "Main"}, {12, "Arya Stark", "Main"}, {13, "Theon Greyjoy", "Main"},
		{46, "Bran Stark", "Main"}, {47, "Sandor \"The Hound\" Clegane", "Main"}, {17, "Tyrion Lannister", "Main"},
		{18, "Petyr Baelish", "Main"}, {27, "Davos Seaworth", "Main"}, {19, "Samwell Tarly", "Main"},
		{29, "Melisandre", "Main"}, {21, "Bronn", "Main"}, {22, "Varys", "Main"},
		{30, "Margaery Tyrell", "Main"}, {37, "Tormund Giantsbane", "Main"}, {33, "Brienne of Tarth", "Main"},
		{38, "Ramsay Bolton", "Main"}, {34, "Gilly", "Main"}, {39, "Daario Naharis", "Main"},
		{40, "Missandei", "Main"}, {42, "Tommen Baratheon", "Main"}, {43, "Ellaria Sand", "Main"},
		{44, "Jaqen H'ghar", "Main"}, {36, "Roose Bolton", "Main"}, {48, "The High Sparrow", "Main"},
		{41, "Grey Worm", "Recurring"}
	}, {
		// Season 7
		{45, "Ned Stark", "Guest"}, {3, "Jaime Lannister", "Main"}, {5, "Cersei Lannister", "Main"},
		{6, "Daenerys Targaryen", "Main"}, {7, "Jorah Mormont", "Main"}, {9, "Jon Snow", "Main"},
		{11, "Sansa Stark", "Main"}, {12, "Arya Stark", "Main"}, {13, "Theon Greyjoy", "Main"},
		{46, "Bran Stark", "Main"}, {47, "Sandor \"The Hound\" Clegane", "Main"}, {17, "Tyrion Lannister", "Main"},
		{18, "Petyr Baelish", "Main"}, {27, "Davos Seaworth", "Main"}, {19, "Samwell Tarly", "Main"},
		{29, "Melisandre", "Main"}, {21, "Bronn", "Main"}, {22, "Varys", "Main"},
		{49, "Gendry", "Main"}, {37, "Tormund Giantsbane", "Main"}, {33, "Brienne of Tarth", "Main"},
		{34, "Gilly", "Main"}, {40, "Missandei", "Main"}, {43, "Ellaria Sand", "Main"},
		{41, "Grey Worm", "Recurring"}
	}, {
		// Season 8
		{3, "Jaime Lannister", "Main"}, {5, "Cersei Lannister", "Main"}, {6, "Daenerys Targaryen", "Main"},
		{7, "Jorah Mormont", "Main"}, {9, "Jon Snow", "Main"}, {11, "Sansa Stark", "Main"},
		{12, "Arya Stark", "Main"}, {13, "Theon Greyjoy", "Main"}, {46, "Bran Stark", "Main"},
		{47, "Sandor \"The Hound\" Clegane", "Main"}, {17, "Tyrion Lannister", "Main"}, {27, "Davos Seaworth", "Main"},
		{19, "Samwell Tarly", "Main"}, {29, "Melisandre", "Main"}, {21, "Bronn", "Main"},
		{22, "Varys", "Main"}, {49, "Gendry", "Main"}, {37, "Tormund Giantsbane", "Main"},
		{33, "Brienne of Tarth", "Main"}, {34, "Gilly", "Main"}, {40, "Missandei", "Main"},
		{41, "Grey Worm", "Main"}
	}};

	private final static ArrayList<ArrayList<Cast>> castsBySeasons = new ArrayList<>();

	static {
		for (Object[][] seasonCasts : gameOfThronesSeasons) {
			ArrayList<Cast> casts = new ArrayList<>();
			for (Object[] cast : seasonCasts) {
				casts.add(new Cast((int)cast[0], (String)cast[1], (String)cast[2]));
			}
			castsBySeasons.add(casts);
		}
	}

	public BatchTest() throws IOException {
		Properties props = TestUtils.loadProperties(getClass(), "pgsql_hikari.properties");
		pipe = new PostgresqlDbManager(
			new HikariDataSource(new HikariConfig(props)),
			props.getProperty("poolName")
		).getPipe(log);
	}

	@Test
	public void testIdUpdates() {
		pipe.execute(
			"CREATE TABLE season_cast (\n" +
				"id serial NOT NULL,\n" +
				"name text NOT NULL,\n" +
				"cast_type text NOT NULL,\n" +
				"CONSTRAINT season_cast_pk PRIMARY KEY (id),\n" +
				"CONSTRAINT season_cast_uniq UNIQUE (name)" +
			")"
		);
		int seasonNumber = 1;
		for (ArrayList<Cast> seasonCasts : castsBySeasons) {
			final int finalSeasonNumber = seasonNumber;
			// remove disappeared casts
			pipe.execute(
				"DELETE FROM season_cast WHERE id NOT IN (?)",
				seasonCasts.stream().map(Cast::getId).collect(Collectors.toList())
			);
			// get casts with cleaned ids
			List<Cast> seasonCastsWithouIds = seasonCasts.stream().map(Cast::getCleanIdCopy).collect(Collectors.toList());
			// upsert new once
			pipe.batchUpsert(seasonCastsWithouIds);
			// assert correct ids was generated and read
			Iterator<Cast> correctIdCastsIterator = seasonCasts.iterator();
			seasonCastsWithouIds.forEach(updatedCast -> assertEquals(
				correctIdCastsIterator.next(), updatedCast, "Wrong updated cast at round " + finalSeasonNumber
			));
			// load stored casts
			Map<String, Cast> storedCastMap = pipe.select(
					Cast.class, "SELECT * FROM season_cast"
				).collect(Collectors.toMap(cast -> cast.name, cast -> cast));
			// check stored casts equals to expected
			assertEquals(seasonCasts.size(), storedCastMap.size(), "Wrong stored casts at round " + seasonNumber);
			seasonCasts.forEach(cast -> assertEquals(
				cast, storedCastMap.get(cast.name), "Wrong stored cast at round " + finalSeasonNumber
			));
			++ seasonNumber;
		}
	}
}
