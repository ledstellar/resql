package ru.resql.batch;

import lombok.*;
import ru.resql.orm.annotations.*;

@EqualsAndHashCode
@Table(name = "season_cast")
public class Cast {
	@GeneratedValue @Getter
	int id;
	@NaturalId String name;
	String appearance;

	Cast(int id, String name, String appearance) {
		this.id = id;
		this.name = name;
		this.appearance = appearance;
	}

	Cast(String name, String appearance) {
		this(-1, name, appearance);
	}

	public Cast getCleanIdCopy() {
		return new Cast(name, appearance);
	}

	@Override
	public String toString() {
		return "(" + id + ") " + name + ": " + appearance;
	}
}
