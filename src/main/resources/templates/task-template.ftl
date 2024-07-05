<html>
<body>
<p>Hello ${task.assignee.username}!</p>
<p>Task <b>${task.name}</b> has been assigned to you. Start date: ${(task.startDate)!}</p>
<div>
    <#list task.timeEntries>
        Time entries time spent:
        <ul>
            <#items as timeEntry>
                <li>${timeEntry.timeSpent}</li>
            </#items>
        </ul>
    </#list>
</div>
</body>
</html>