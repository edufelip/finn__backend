CREATE TABLE users (
    id text PRIMARY KEY,
    name text NOT NULL,
    DATE TIMESTAMP DEFAULT now()
);

CREATE TABLE communities (
	id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 10 INCREMENT BY 3),
	title VARCHAR(25) NOT NULL,
	description VARCHAR(100) NOT NULL,
	image TEXT,
	user_id TEXT,
	date TIMESTAMP DEFAULT now(),
	CONSTRAINT community_belongsTo_user
		FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

create table posts (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 10 INCREMENT BY 3),
    content TEXT NOT NULL,
	image TEXT,
	likes_count INT,
	user_id TEXT,
	community_id INT,
	date TIMESTAMP DEFAULT now(),
	CONSTRAINT post_belongsTo_community
		FOREIGN KEY (community_id) REFERENCES communities (id) ON DELETE CASCADE,
	CONSTRAINT post_belongsTo_user
		FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE comments (
	id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 10 INCREMENT BY 3),
	content VARCHAR(200) NOT NULL,
	user_id TEXT,
	post_id INT,
	date TIMESTAMP DEFAULT now(),
	CONSTRAINT comment_belongsTo_user
		FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	CONSTRAINT comment_belongsTo_post
		FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

CREATE TABLE userscommunities (
	id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY (START WITH 10 INCREMENT BY 3),
	user_id TEXT,
	community_id INT,
	date TIMESTAMP DEFAULT now(),
	CONSTRAINT user_belongsToMany_communities
		FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
	CONSTRAINT community_belongsToMany_users
		FOREIGN KEY (community_id) REFERENCES communities (id) ON DELETE CASCADE 
);