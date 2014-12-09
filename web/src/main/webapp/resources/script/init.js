




/**
 *  web socket
 */
var wsocket;
var consumerState = {
    "0": {
        "name": "Initializing",
        "class": ""
    },
    "1": {
        "name": "Waiting for Lock",
        "class": "list-group-item-warning"
    },
    "2": {
        "name": "Waiting for NotEmpty Condition",
        "class": "list-group-item-info"
    },
    "3": {
        "name": "Locking, Taking Task from Queue",
        "class": "list-group-item-danger"
    },
    "10": {
        "name": "Executing Task",
        "class": "list-group-item-success"
    },
    "20": {
        "name": "Shutdown",
        "class": ""
    },
    "30": {
        "name": "Rollback",
        "class": "list-group-item-danger"
    }
};
var producerState = {
    "0": {
        "name": "Initializing",
        "class": ""
    },
    "1": {
        "name": "Waiting for Lock",
        "class": "list-group-item-warning"
    },
    "2": {
        "name": "Waiting for NotFull Condition",
        "class": "list-group-item-info"
    },
    "3": {
        "name": "Locking, Putting Task to Queue",
        "class": "list-group-item-danger"
    },
    "10": {
        "name": "Creating Task",
        "class": "list-group-item-success"
    },
    "20": {
        "name": "Shutdown",
        "class": ""
    }
};
var protocol = {
    //request
    "UPDATE_CONSUMER_REQUEST": "ucreq",
    "UPDATE_PRODUCER_REQUEST": "upreq",
    "UPDATE_QUEUE_REQUEST": "uqreq",
    "UPDATE_PUT_LOCK_REQUEST": "plreq",
    "UPDATE_TAKE_LOCK_REQUEST": "tlreq",
    "UPDATE_STATE_REQUEST": "ureq",
    //response
    "ucres": "Updating Consumer",
    "upres": "Updating Producer",
    "uplres": "Updating Put Lock",
    "utlres": "Updating Take Lock",
    "uqres": "Updating Queue",
    "ures": "Updating Page"
};



function connect() {
    wsocket = new WebSocket("ws://" + window.baseWSUrl + "websocket");
    wsocket.onmessage = onMessage;
}

function onMessage(evt) {
    var msg = JSON.parse(evt.data);
    switch (msg.type) {
        case "ures":
            updatePage(msg);
    }
}

function sendUpdate() {
    wsocket.send(window.updateRequest);
}

function sendConsumerUpdate(name, efficiency, shutdown) {
    var msg = {};
    msg.type = protocol.UPDATE_CONSUMER_REQUEST;
    msg.parameters = {};
    msg.parameters.name = name;
    msg.parameters.forceShutdown = shutdown;
    msg.parameters.efficiency = efficiency;
    wsocket.send(JSON.stringify(msg));
}

function sendProducerUpdate(name, pmin, pmax, tmin, tmax, shutdown) {
    var msg = {};
    msg.type = protocol.UPDATE_PRODUCER_REQUEST;
    msg.parameters = {};
    msg.parameters.name = name;
    msg.parameters.forceShutdown = shutdown;
    msg.parameters.minProductionTime = pmin;
    msg.parameters.maxProductionTime = pmax;
    msg.parameters.minTaskExecutionTime = tmin;
    msg.parameters.maxTaskExecutionTime = tmax;
    wsocket.send(JSON.stringify(msg));
}

function sendQueueUpdate(capacity, putmin, putmax, takemin, takemax) {
    var msg = {};
    msg.type = protocol.UPDATE_QUEUE_REQUEST;
    msg.parameters = {};
    msg.parameters.capacity = capacity;
    msg.parameters.maxPutTime = putmax;
    msg.parameters.minPutTime = putmin;
    msg.parameters.maxTakeTime = takemax;
    msg.parameters.minTakeTime = takemin;
    wsocket.send(JSON.stringify(msg));
}


function init() {
    //init socket
    window.baseWSUrl = window.location.host + window.location.pathname;
    //window.baseWSUrl = "localhost:8080/web-0.0.1-SNAPSHOT/";
    connect();

    // init cm
    window.cmodal = $('#consumerModal');
    cmodal.on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var title = button.data('title');
        var efficiency = button.data('efficiency');
        cmodal.find('.modal-title').text(title);
        if (efficiency == undefined) {
            cmodal.find('.modal-body input.pm').val("").attr("readonly", false);
            cmodal.find('.modal-body input.pe').val("10");
            cmodal.find('.modal-footer button.btn-warning').hide();
            cmodal.find('.modal-footer button.btn-danger').hide();
        } else {
            cmodal.find('.modal-body input.pm').val(title).attr("readonly", true);
            cmodal.find('.modal-body input.pe').val(efficiency);
            cmodal.find('.modal-footer button.btn-warning').show();
            cmodal.find('.modal-footer button.btn-danger').show();
        }
    });
    $('#consumer-shutdown').click(function () {
        consumerModalHelper(false);
    });
    $('#consumer-fshutdown').click(function () {
        consumerModalHelper(true);
    });
    $('#consumer-update').click(function () {
        consumerModalHelper();
    });

    //init pm
    window.pmodal = $('#producerModal');
    pmodal.on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var title = button.data('title');
        var pmin = button.data('pmin');
        var pmax = button.data('pmax');
        var tmin = button.data('tmin');
        var tmax = button.data('tmax');
        pmodal.find('.modal-title').text(title);
        if (pmin == undefined) {
            pmodal.find('#producer-name').val("").attr("readonly", false);
            pmodal.find('#pmin').val("5000");
            pmodal.find('#pmax').val("5000");
            pmodal.find('#tmin').val("5000");
            pmodal.find('#tmax').val("5000");
            pmodal.find('.modal-footer button.btn-warning').hide();
            pmodal.find('.modal-footer button.btn-danger').hide();
        } else {
            pmodal.find('#producer-name').val(title).attr("readonly", true);
            pmodal.find('#pmin').val(pmin);
            pmodal.find('#pmax').val(pmax);
            pmodal.find('#tmin').val(tmin);
            pmodal.find('#tmax').val(tmax);
            pmodal.find('.modal-footer button.btn-warning').show();
            pmodal.find('.modal-footer button.btn-danger').show();
        }
    });
    $('#producer-shutdown').click(function () {
        producerModalHelper(true)
    });
    $('#producer-fshutdown').click(function () {
        producerModalHelper(false)
    });
    $('#producer-update').click(function () {
        producerModalHelper();
    });

    //init qm
    window.qmodal = $('#queueModal');
    qmodal.on('show.bs.modal', function (event) {
        var button = $(event.relatedTarget);
        var title = button.data('title');
        var capacity = $('#totalCapacity').text();
        var putmin = $('#minPutTime').text();
        var putmax = $('#maxPutTime').text();
        var takemin = $('#minTakeTime').text();
        var takemax = $('#maxTakeTime').text();
        qmodal.find('.modal-title').text(title);
        qmodal.find('#queue-capacity').val(capacity);
        qmodal.find('#putmin').val(putmin);
        qmodal.find('#putmax').val(putmax);
        qmodal.find('#takemin').val(takemin);
        qmodal.find('#takemax').val(takemax);
    });
    $('#queue-update').click(function () {
        var capacity = $('#queue-capacity').val();
        var putmin = $('#putmin').val();
        var putmax = $('#putmax').val();
        var takemin = $('#takemin').val();
        var takemax = $('#takemax').val();
        sendQueueUpdate(capacity,putmin,putmax,takemin,takemax);
        qmodal.modal('hide');
    });
    //init update page
    var updatereq = {};
    updatereq.type=protocol.UPDATE_STATE_REQUEST;
    window.updateRequest = JSON.stringify(updatereq);
    setInterval(sendUpdate, 250);
}

/**
 *  update page
 */
function updatePage(data) {
    if (data.queueInformation.takeLockState) {
        $("#takelock").removeClass('btn-success').addClass('btn-danger');
    } else {
        $("#takelock").removeClass('btn-danger').addClass('btn-success');
    }
    if (data.queueInformation.putLockState) {
        $("#putlock").removeClass('btn-success').addClass('btn-danger');
    } else {
        $("#putlock").removeClass('btn-danger').addClass('btn-success');
    }
    $("#minPutTime").html(data.queueInformation.minPutTime);
    $("#maxPutTime").html(data.queueInformation.maxPutTime);
    $("#minTakeTime").html(data.queueInformation.minTakeTime);
    $("#maxTakeTime").html(data.queueInformation.maxTakeTime);

    var max = data.queueInformation.totalCapacity;
    var used = data.queueInformation.usedCapacity;
    var value = 100 * used / max;
    $('#totalCapacity').html(max);
    $('#usedCapacity').html(used);
    if (value < 25) {
        $('#progress').css('width', value + '%').attr('aria-valuenow', value).html(value.toFixed(2) + '%').removeClass('progress-bar-info').removeClass('progress-bar-warning').removeClass('progress-bar-danger').addClass('progress-bar-success');
    } else if (value < 50) {
        $('#progress').css('width', value + '%').attr('aria-valuenow', value).html(value.toFixed(2) + '%').removeClass('progress-bar-success').removeClass('progress-bar-danger').removeClass('progress-bar-warning').addClass('progress-bar-info');
    } else if (value < 75) {
        $('#progress').css('width', value + '%').attr('aria-valuenow', value).html(value.toFixed(2) + '%').removeClass('progress-bar-info').removeClass('progress-bar-success').removeClass('progress-bar-danger').addClass('progress-bar-warning');
    } else {
        $('#progress').css('width', value + '%').attr('aria-valuenow', value).html(value.toFixed(2) + '%').removeClass('progress-bar-info').removeClass('progress-bar-success').removeClass('progress-bar-warning').addClass('progress-bar-danger');
    }


    if (used <= 6) {
        for (var i = 0; i < used; i++) {
            var htmlStr = '<a class="list-group-item"><h4 class="list-group-item-heading">Creator - ' + data.tasks[i].creatorName + '</h4><p class="list-group-item-text text-left">';
            htmlStr += 'Time Needed: ' + data.tasks[i].timeNeeded;
            htmlStr += ' ms <br/> Turnaround Time: ' + data.tasks[i].turnaroundTime;
            htmlStr += '</p>';
            $('#t' + (i + 1)).html(htmlStr);
        }
        if(used == 0) {
            $('#t' + (i + 1)).html("<div class='verticalalign'><h3>Empty</h3></div>");
            used++
        }
        for (var i = used; i < 6; i++) {
            $('#t' + (i + 1)).html("<div class='verticalalign'><h3></h3></div>");
        }

    } else {
        for (var i = 0; i < 4; i++) {
            var htmlStr = '<a class="list-group-item"><h4 class="list-group-item-heading">Creator - ' + data.tasks[i].creatorName + '</h4><p class="list-group-item-text text-left">';
            htmlStr += 'Time Needed: ' + data.tasks[i].timeNeeded;
            htmlStr += ' ms <br/> Turnaround Time: ' + data.tasks[i].turnaroundTime;
            htmlStr += '</p>';
            $('#t' + (i + 1)).html(htmlStr);
        }
        $('#t' + (i + 1)).html("<div class='verticalalign'><h3>...........</h3></div>");
        var htmlStr = '<a class="list-group-item"><h4 class="list-group-item-heading">Creator - ' + data.tasks[i].creatorName + '</h4><p class="list-group-item-text text-left">';
        htmlStr += 'Time Needed: ' + data.tasks[i].timeNeeded;
        htmlStr += ' ms <br/> Turnaround Time: ' + data.tasks[i].turnaroundTime;
        htmlStr += '</p>';
        $('#t' + (i + 2)).html(htmlStr);
    }

    var comsumerSec = $("#consumer");
    comsumerSec.html("");
    for (index in data.consumers) {
        var name = data.consumers[index].name;
        var state = data.consumers[index].state;
        var efficiency = data.consumers[index].efficiency;
        var count = data.consumers[index].taskExecutionCount;
        var htmlStr = createConsumerHtml(name, state, efficiency, count);
        comsumerSec.append(htmlStr);
    }
    var producerSec = $("#producer");
    producerSec.html("");
    for (index in data.producers) {
        var name = data.producers[index].name;
        var state = data.producers[index].state;
        var pm = data.producers[index].minProductionTime;
        var pM = data.producers[index].maxProductionTime;
        var tm = data.producers[index].minTaskExecutionTime;
        var tM = data.producers[index].maxTaskExecutionTime;
        var count = data.producers[index].taskCreationCount;
        var htmlStr = createProducerHtml(name, state, pm, pM, tm, tM, count);
        producerSec.append(htmlStr);
    }

}

function createProducerHtml(name, state, pm, pM, tm, tM, count) {
    return html = '<a data-toggle="modal" data-target="#producerModal" data-title="' + name + '" data-pmin="' + pm + '" data-pmax="' + pM + '" data-tmin="' + tm + '" data-tmax="' + tM
    + '" class="btn list-group-item ' + producerState[state].class + '"><h4 class="list-group-item-heading">'
    + name + '</h4><h6 class="list-group-item-heading">'
    + producerState[state].name + '</h6><p class="text-left list-group-item-text">Time Needed to Create Task: <span class="pm">'
    + pm + '</span> ~ <span class="pM">' + pM + '</span><br />Time Needed to Executing Task: <span class="tm">'
    + tm + '</span> ~ <span class="tM">' + tM + '</span><br/>Total Task Executed Count: <span class="tc">'
    + count + '</span><br/></p></a>';
}

function createConsumerHtml(name, state, efficiency, count) {
    return '<a data-toggle="modal" data-target="#consumerModal" data-efficiency="' + efficiency + '" data-title="' + name + '" class="btn list-group-item ' + consumerState[state].class
        + '"><h4 class="list-group-item-heading">' + name + '</h4><h6 class="list-group-item-heading">'
        + consumerState[state].name + '</h6><p class="text-left list-group-item-text">Efficiency of Task Executing: <span class="ef">'
        + efficiency + '</span><br />Total Task Executed Count: <span class="tc">'
        + count + '</span><br/><br/></p></a>';
}

function producerModalHelper(shutdown) {
    var name = pmodal.find('#producer-name').val();
    var pmin = pmodal.find('#pmin').val();
    var pmax = pmodal.find('#pmax').val();
    var tmin = pmodal.find('#tmin').val();
    var tmax = pmodal.find('#tmax').val();
    sendProducerUpdate(name, pmin, pmax, tmin, tmax, shutdown);
    pmodal.modal('hide');
}

function consumerModalHelper(shutdown) {
    var name = cmodal.find('.modal-body input.pm').val();
    var ef = cmodal.find('.modal-body input.pe').val();
    sendConsumerUpdate(name, ef, shutdown);
    cmodal.modal('hide');
}






window.addEventListener("load", init, false);