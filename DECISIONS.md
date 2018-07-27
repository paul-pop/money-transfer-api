# Decisions and compromises

There are some decisions I made as I went along that I wanted to keep track of here:

1. Wanted to use DropWizard because I have used it before but decided to try something I've never done before instead
so I ended up with Ratpack - it uses an event-based non-blocking HTTP engine.

2. Wanted to use H2 as an in-memory database with JOOQ to generate the models but based on my experience there's so many issues
with type conversion that for simplicity I have just used data structures.

3. Added Swagger so I don't have to document all endpoints in the README, I have not bundled this inside the app because
it's not that easy in Ratpack, didn't want to waste time - so it's a swagger.yaml file that can be imported in the online 
editor

4. Used the Maven Shade plugin to bundle the app into a single executable JAR

5. Although not required, I added validation for requests via JSR-303, the compromise I made here is not to return a detailed 
message about the error - as there's extra work for converting the set of ContraintViolations into JSON

6. Testing - rather than going crazy and unit testing every single bit, this API needs more integration/functional tests as 
there's a lot of Ratpack internal stuff going on. So I have used that to spin up the server and test the API. I have also 
not tested all possible validations just so I don't waste too much time.

7. REST endpoints - I have avoided implementing unnecessary endpoints like PUT, PATCH or DELETE - we just need methods to
create accounts and retrieve them and create transfers and retrieve them.
