# Decisions and compromises

There are some decisions I made as I went along that I wanted to keep track of here:

1. Wanted to use DropWizard because I have used it before but decided to try something I've never done before instead
so I ended up with Ratpack - it uses an event-based non-blocking HTTP engine.

2. Originally thought about storing everything in a Map in memory but then decided to adapt H2 in-memory database
for some easy validation and I have used JOOQ to generate the relevant utility classes for me so I can perform 
queries and mutations

3. Added Swagger so I don't have to document all endpoints in the README, I have not bundled this inside the app because
it's not that easy in Ratpack, didn't want to waste time - so it's a swagger.yaml file that can be imported in the online 
editor

4. Used the Maven Shade plugin to bundle the app into a single executable JAR

5. I have kept the currency as a String and not a Currency object because I would have had to write a custom JOOQ converter
