CREATE TABLE IF NOT EXISTS users (
    id text PRIMARY KEY,
    name text NOT NULL,
    photo text,
    date TIMESTAMP DEFAULT now()
);

CREATE TABLE IF NOT EXISTS communities (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    title VARCHAR(25) NOT NULL UNIQUE,
    description VARCHAR(100) NOT NULL,
    image TEXT,
    user_id TEXT,
    date TIMESTAMP DEFAULT now(),
    CONSTRAINT community_belongsTo_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS posts (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    content TEXT NOT NULL,
    image TEXT,
    user_id TEXT,
    community_id INT,
    date TIMESTAMP DEFAULT now(),
    CONSTRAINT post_belongsTo_community
        FOREIGN KEY (community_id) REFERENCES communities (id) ON DELETE CASCADE,
    CONSTRAINT post_belongsTo_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS comments (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    content VARCHAR(200) NOT NULL,
    user_id TEXT,
    post_id INT,
    date TIMESTAMP DEFAULT now(),
    CONSTRAINT comment_belongsTo_user
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT comment_belongsTo_post
        FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS userscommunities (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id TEXT,
    community_id INT,
    date TIMESTAMP DEFAULT now(),
    CONSTRAINT user_belongsToMany_communities
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT community_belongsToMany_users
        FOREIGN KEY (community_id) REFERENCES communities (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS likes (
    id INT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    user_id TEXT,
    post_id INT,
    CONSTRAINT user_likes_post
        FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT post_has_likes
        FOREIGN KEY (post_id) REFERENCES posts (id) ON DELETE CASCADE
);

