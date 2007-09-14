Create Table Image
(
	id			Integer		Not Null Primary Key Generated Always As Identity (Start With 1, Increment By 1),
	SHA256		Char (64)	Not Null,
	fileSize	BigInt		Not Null,
	Unique (SHA256, fileSize)
);

Create Table ImageSection
(
	imageId		Integer		Not Null References Image (id),
	ddx			SmallInt	Not Null,
	ddy			SmallInt	Not Null,
	section		SmallInt	Not Null,
	meanR		Float (5)	Not Null,
	meanG		Float (5)	Not Null,
	meanB		Float (5)	Not Null,
	Primary Key (imageId, ddx, ddy, section)
);

Create Index DDIndex On ImageSection (ddx, ddy);