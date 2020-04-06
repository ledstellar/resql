package ru.resql.orm;

@SuppressWarnings("unused")
enum RatedAnimeCharacter implements Tagged<Integer> {
/* 1  */	Goku,
/* 2  */	AstroBoy,
/* 3  */	SpeedRacer,
/* 4  */	SpikeSpiegel,
/* 5  */	HimuraKenshin,
/* 6  */	NarutoUzumaki,
/* 7  */	EdwardElric,
/* 8  */	Pikachu,
/* 9  */	SailorMoon,
/* 10 */	AyanamiRei,
/* 11 */	ShotaroKaneda,
/* 12 */	L,
/* 13 */	MotokoKusanagi,
/* 14 */	D,
/* 15 */	ArseneLupinIII;

	@Override
	public Integer getTag() {
		return ordinal() + 1;
	}
}
