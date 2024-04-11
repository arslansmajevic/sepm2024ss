# SE PR Projekttemplate

Bitte fügen Sie diese Datei, inklusive der beiliegenden `.gitlab-ci.yml` und den beiden Verzeichnissen `frontend` und `backend` zum Wurzelverzeichnis ihres Git-Repositories hinzu.
Im folgenden befindet sich ein Template für die Stundenliste; bitte verwenden Sie es so, dass im GitLab-Projekt ihre Stundenliste als Tabelle sichtbar ist.

Vergessen Sie nicht im Projekt ihren Namen und Matrikelnummer zu ersetzen.

## Stundenliste

**Name**: Arslan Smajevic\
**Matrikelnummer**: 12127678

| Datum      | Startzeit | Dauer | Story-ID                               | Tätigkeit                                                                           |
|------------|-----------|-------|----------------------------------------|-------------------------------------------------------------------------------------|
| 2024-03-11 | 22:00     | 1:30h | US0, US1, US2, US3, US4, US5, US6, US7 | Designing the Database                                                              |
| 2024-03-12 | 01:00     | 1:30h | US1                                    | Adding new horse on backend                                                         |
| 2024-03-13 | 04:00     | 0:30h | TS26, TS10, TS9                        | Status and exceptions for create on new Horse                                       |
| 2024-03-13 | 14:00     | 0:30h | US1, TS14                              | Create new horse: backend and frontend                                              |
| 2024-03-13 | 14:30     | 1:30h | US2                                    | PUT on horse across all layers                                                      |
| 2024-03-13 | 16:00     | 0:30h | US1                                    | Editing a horse on frontend + routing                                               |
| 2024-03-13 | 16:30     | 0:30h | US2, US4                               | Adding Info View + Buttons                                                          |
| 2024-03-13 | 22:30     | 1:30h | US3                                    | Adding delete horse feature on backend                                              |
| 2024-03-14 | 01:30     | 2:00h | US3                                    | Adding popup window for delete and its logic                                        |
| 2024-03-15 | 01:00     | 2:00h | US5, US6, US7                          | Implementing the database design for tournaments                                    |
| 2024-03-15 | 14:00     | 2:00h | US5                                    | Implementing search of tournaments on all layers in backend                         |
| 2024-03-16 | 16:00     | 3:00h | US6                                    | Implementing adding of tournaments on all layers in backend                         |
| 2024-03-17 | 15:00     | 2:30h | US3                                    | Fixing the issue on frontend and adding check on horses that belong to a tournament |
| 2024-03-17 | 15:00     | 5:00h | US7                                    | Get mapping for standings of a tournament                                           |
| 2024-03-19 | 20:30     | 3:00h | US6, US7                               | Changing the creation of races and GET mapping for first races generation           |
| 2024-03-18 | 15:00     | 3:00h | US7                                    | Tournament standing functioning on backend & frontend                               |
| 2024-03-20 | 14:00     | 6:00h | US7                                    | Edit on tournament standing backend & frontend                                      |
| 2024-03-22 | 15:30     | 0:30h | US0, TS9                               | Logs on US0 in backend                                                              |
| 2024-03-23 | 22:00     | 4:00h | US8                                    | Score of horses regarding first matches                                             |
| 2024-03-24 | 00:00     | 3:00h | US7                                    | Enabling / disabling input of a branch for a horse in frontend                      |
| 2024-03-24 | 04:30     | 0:30h | US7, TS14                              | Validation on tree when duplicates in last nodes are found                          |
| 2024-03-26 | 10:00     | 1:00h | US8, TS26, TS9                         | Review of the technical stories on US8 in backend                                   |
| 2024-03-26 | 11:00     | 0:30h | US8, TS17, TS19                        | Adding warning dialog on generate first round matches                               |
| 2024-03-27 | 21:00     | 1:30h | TS10, TS9                              | Writing documentation and exceptions                                                |
| 2024-03-28 | 1:00      | 0:30h | TS10                                   | Writing documentation and code redundancy                                           |
| 2024-03-28 | 2:30      | 0:30h | US7, TS28                              | Foreign keys on database by races                                                   |
| 2024-03-28 | 20:30     | 1:30h | TS19                                   | Errors on backend not available + resources restrictions                            |
| 2024-03-30 | 01:30     | 1:30h | TS12                                   | HorseEndpoint Tests                                                                 |
| 2024-03-30 | 03:00     | 1:30h | TS12                                   | HorseDao Tests                                                                      |
| 2024-03-31 | 05:00     | 2:00h | TS12                                   | TournamentDao Tests                                                                 |
| 2024-04-1  | 02:00     | 2:00h | TS12                                   | TournamentEndpoint Tests + response on firstStanding                                |
| 2024-04-1  | 16:00     | 3:00h | TS12                                   | TournamentServiceTest + HorseServiceTest                                            |
| 2024-04-2  | 00:00     | 3:00h | TS12                                   | Troubleshooting pipeline                                                            |
| 2024-04-9  | 22:00     | 2:00h | TS27                                   | Naming and dropping of constraints                                                  |

**Gesamtsumme der Zeit**: 65:00h 

## Notes

**Database 2024-03-11 1:30h**\
Database design that I will be using for this project can be found via an external tool: https://dbdiagram.io/d/SEPM-Database-Einzelphase-65ea1d07b1f3d4062c667e93 \
Please do note, that this database design is `keen for change during the develeopment cycle`, and that the relationships will be more better `defined in the implementation itself`. \
This database design included also a review of the database with an external colleague regarding the user stories and originally given implementation.\

**Adding new Horse on backend 2024-03-12 1:30h**\
Adding new horse across the rest, service and persistance layer. Validation has also been implemented. \
Tech stories have not been taken into consideration.

**Status + exceptions on create in backend 2024-03-13 0:30h**\
Review US1 regarding TS26, TS10, TS9

**Create new horse: backend and frontend 2024-03-14 0:30h**\
Database horse name is limited to 255 characters, so therefore the change in the validator for the horse name. \
Easier to catch this here than on persistence layer. \
Changed frontend form for create to allow breed as optional.

**PUT on horse across all layers 2024-03-13 1:30h**\
Implementing edit on horse - horse is edited as whole object: every attribute is refreshed on edit.\
Implementation of breed as null on edit.\
`Tech Stories have not been taken into consideration on this commit.`

**Editing a horse on frontend + routing 2024-03-13 0:30h**\
Implementing edit on horse - frontend.\
`Erorrs on service have not been taken into consideration.` 

**Adding Info View + Buttons 2024-03-13 0:30h**\
Adding routing buttons and Info View in frontend.

**Adding delete horse feature on backend 2024-03-13 1:30h**\
Adding delete mapping on backend via `/horses/{id}`.\
Delete feature relies only on the id, hence this is the only parameter necessary and anything extra would be redundant.\
NotFoundException implemented on the last layer (persistance). 

**Adding popup window for delete and its logic 2024-03-14 2:00h**\
Added a feature of a popup window on frontend when pressing delete.\
This will be reviewed hence `the closing of the window, an error occurs` in the console.

**Implementing the database design for tournaments 2024-03-15 2:00h**\
Utter disappointment from the H2 database.\
Following the design from the first note on 2024-03-11, it was concluded that the cyclic connections and nulls on `previous_race_first_horse` and `previous_race_second_horse` is not well suited for H2 database.\
The delete order can not be defined and therefore it causes a problem on the tests, with every delete.\
Temporary solution will be setting the `previous_race_first_horse` and `previous_race_second_horse` as normal attributes, that will be later used for the querries.\
This problem will be raised as an issue and be reviewed externally.

**Implementing search of tournaments on all layers in backend 2024-03-15 2:00h**\
Adding the implementation for tournaments of search in the backend.\
At this point, no tech story was compared with this user story and leaves to be reviewed at a further time.

**Implementing adding of tournaments on all layers in backend 2024-03-15 3:00h**\
A whole lot of stuff was added here.\
Adding of tournaments has been compared with the code from frontend, so there the motivation for this implementation.\
A question that remains, `in the HorseDao I made querries on different tables - should this be separated as a Dao for itself?`\
Validation implemented.\
`Race conditions on deletions of horses to be reviewed.`

**Fixing the issue on frontend and adding check on horses that belong to a tournament 2024-03-17 2:30h**\
Fixing the #2 issue on the frontend.\
Exception handling on delete in frontend.\
Implementing delete regarding horse on tournament.

**Get mapping for standings of a tournament 2024-03-17 5:00h**\
Added get mapping for a tournament standing.\
Implementation of tree like structure for standing.\
This essentially took the most time for this part, because creating the tree took a turn that misleading.\
For the tree, the database design came like charm (after coming to senses and realisin that using the previous ids is the way to go).
**Changing the creation of races and GET mapping for first races generation 2024-03-19 3:00h**
Upon inspecting frontend structure, I realised that we don't have to create races on creation of tournament.\
That is, the tree structure should be made available when the user clicks generate first matches button.\
Therefore, the motivation to change the way of creating races.\
In this commit, I also restructured my code.\
I tried my best to separate as much as possible the different daos and that the services use mainly daos to extract what they need.

**Tournament standing functioning on backend & frontend 2024-03-18 3:00h**\
I would say that this took much longer than 3 hours.\
Upon creating the GET mapping for Standings, the tree structure looked ok, but was not working on the frontend.\
So, lesson learned, that the DTOs really have to be 1 to 1 - frontend to backend.\
The main problem was that in frontend there is thisParticipant and in backend I declared the record to have participant.\
Therefore, everything on the frontend was being recognised as null what made no sense.\
This has been resolved and the frontend is functioning like a charm.

**Edit on tournament standing backend & frontend 2024-03-20 6:00h**\
PUT mapping for tournament standing implementation on backend and frontend.\
This was the reverse part of the standing creation - reading from a tree and updating the data in the database.\
Once again, the database design proved as good because the implementation was relativly simple and managable.\
On the side note, the validation of a tournament was something different, hence everything had to be checked.\
As of this moment, the main functionalities are implemented, leaving the US8 to be implemented.\
Code review and technical stories will be closely reviewed and implemented in the following days.

**Score of horses regarding first matches 2024-03-23 4:00h**\
This took some time because figuring the right queries was a hassle here.\
Although this is working, this still has to be thorougly tested!

**Enabling / disabling input of a branch for a horse in frontend 2024-03-24 3:00h**\
After a lot of trial and errors on this implementation, I managed to make it work.\
But as of this moment, I can not explain the logic behind it.

**Validation on tree when duplicates in last nodes are found 2024-03-24 0:30h**\
Validation of the tree if there are duplicates in the tree last nodes.\
This cannot happen in frontend and will be implemented as well.

**Foreign keys on database by races 2024-03-28 0:30h**\
This resolves the previous problem of keeping the database consistent of its implementation - the use of foreign keys.\
I came across this, by an old SEPM solution on GitHub by the colleague Miladin Gavric.\
This enables the mvn package clean - the continuous and unobstructed deletion of races.

**Troubleshooting pipeline 2024-04-02 3:00h**\
This took me way too long to figure out, that I did not extend my tests with TestBase.\
Don't know why is this used - and why it worked previously without it.\

**Naming and dropping of constraints 2024-04-09 2:00h**\
This problem was pointed out by a fellow colleague, Marijana Petojevic.\
The base of the problem was: we start the application, insert some data, using the test data as well and then shutdown the application.\
Then we can do `mvn clean package` to create a new jar file.\
Then again, we start the application and it results in a failure.\
Reasoning for that is that the constraints of foreign keys are being violated due to `insert.sql` file.\
Solution was to name the constraints, and in the `insert.sql` file, drop them before deleting and adding the test data.\
Hence these constraints have to be present, we added them after the insertion once again as they originally were.\

**Final submission**\
As of this moment, 65 hours have been spent on this project.