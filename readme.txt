1) Run:
1.1) cd ./{prog_dir}
1.2) ./gradlew bootJar
1.3) cd ./build/libs/
1.4) java -jar ./ZharovFedor_Skytec_ClanTestTask.jar

2) Fast start:
2.1) Reg new clan with name clan4:
     post to "/clan"
         req  string "clan4",
         resp json   {"id": 4, "name": "clan1", "gold": 0}

2.2) Reg new transaction for clan4 and actor with name "actor" (transaction initiator)
     post to "/clan_gold_transaction"
          req  json { "clanId": 4, "operationOwner": "actor", "operationName": "op1", "operationGoal": "UP", "goldDiff": 42 }
          resp json {"id":33,"clanId":4, "operationOwner":"actor", "operationName":"op1", "operationGoal":"UP", "goldDiff":42, "operationState":"CREATED","createAt":"2022-10-25T14:47:24.743"}

2.3) Check transaction status, wait for operationState = SUCCESS or operationState = FAILED
    get /clan_gold_transaction/33
         resp json {"id":33,"clanId":4, "operationOwner":"actor", "operationName":"op1", "operationGoal":"UP", "goldDiff":42, "operationState":"CREATED","createAt":"2022-10-25T14:47:24.743"}

2.4) For detailed report see 3.8)

3) API:
3.1) def path - http://localhost:9001

3.2) reg clan:
- method: post
- url: /clan
- req body: nameOfNewClan - text
- resp: {"id": 1, "name": "", "gold": 0} - json

3.3) get clan by id
- method: get
- url: /clan/{id}
- req path var: id - int
- resp: {"id": 1, "name": "", "gold": 0} - json

3.4) get clan by name
- method: get
- url: /clan/{name}
- req path var: name - String
- resp: {"id": 1, "name": "", "gold": 0} - json

3.5) get all
- method: get
- url: /clan
- req params: page - int (not required), pageSize - int (not required)
- resp: [{"id": 1, "name": "", "gold": 0}] - json

3.6) reg transaction
- method: post
- url: /clan_gold_transaction
- req body:  { "clanId": 1, "operationOwner": "ow1", "operationName": "op1", "operationGoal": "UP", "goldDiff": 1 } - json
- resp: {"id":1,"clanId":1, "operationOwner":"ow1", "operationName":"op1", "operationGoal":"UP", "goldBefore": null, "goldDiff":1, "goldAfter": null, "operationState":"CREATED","createAt":"2022-10-25T14:47:24.743","endAt": null} - json

3.7) get transaction by id
- method: get
- url: /clan_gold_transaction/{id}
- req path var: id - int
- resp: {"id":1,"clanId":1, "operationOwner":"ow1", "operationName":"op1", "operationGoal":"UP", "goldBefore": null, "goldDiff":1, "goldAfter": null, "operationState":"CREATED","createAt":"2022-10-25T14:47:24.743","endAt": null} - json

3.8) get transactions by filters
- method: post
- url: /clan_gold_transaction/
- req body: { "operationOwners": ["ow1", "ow2"],
            "clansIds": [1,2], "operationNames": ["op1", "op2"], "states": ["CREATED", "SUCCESS", "FAILED"],
            "createdAtFrom": "2022-10-25T13:50:20.764", "createdAtTo": "2022-10-25T13:54:26.764",
            "page": 0, "pageSize": 10 } - json
- resp: [{"id":1,"clanId":1,"operationOwner":"ow1","operationName":"op1","operationGoal":"UP",
             "goldBefore":0,"goldDiff":1,"goldAfter":0,
             "operationState":"FAILED",
             "createAt":"2022-10-25T14:47:24.743","endAt":"2022-10-25T14:47:27.764"},
         {"id":2,"clanId":2,"operationOwner":"ow2","operationName":"op2","operationGoal":"DOWN",
             "goldBefore":0,"goldDiff":1,"goldAfter":1,
             "operationState":"SUCCESS",
             "createAt":"2022-10-25T14:47:27.758","endAt":"2022-10-25T14:47:27.765"}] - json

