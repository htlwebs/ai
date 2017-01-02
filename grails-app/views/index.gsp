<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="main"/>
    <style type="text/css">
    .labels {
        color: red;
        background-color: white;
        font-family: "Lucida Grande", "Arial", sans-serif;
        font-size: 10px;
        font-weight: bold;
        text-align: center;
        width: 40px;
        border: 2px solid black;
        white-space: nowrap;
    }

    .mapDiv {
        position: relative;
    }

    .googleMap {
        position: absolute;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
    }

    .fullscreen {
        position: fixed;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
    }


    </style>

    <script>

        var map;
        var prev_infoWindow = false;

        var markers = [];
        var circles = [];
        var subMarkers = [];
        var boundryGons = [];

        var viewMode = "N"; // Normal View

        var salesRangeVals = "";
        var popRangeVals = "";

        var tableData = [];
        var oTable;
        var oTable1;

        var months = new Array();
        months[0] = "January";
        months[1] = "February";
        months[2] = "March";
        months[3] = "April";
        months[4] = "May";
        months[5] = "June";
        months[6] = "July";
        months[7] = "August";
        months[8] = "September";
        months[9] = "October";
        months[10] = "November";
        months[11] = "December";


        var distrctColor = "green";


        function showProcessing() {

            $.noty.closeAll();
            var netw = noty({
                text: 'Processing. Please wait.',
                layout: 'center',
                type: 'information',
                timeout: false,
                theme: 'relax',
                modal: true,
                buttons: false,
                backgroundColor: "blue",
                closeWith: [],
                animation: {
                    open: {height: 'toggle'}, // jQuery animate function property object
                    close: {height: 'toggle'}, // jQuery animate function property object
                    easing: 'swing', // easing
                    speed: 500 // opening & closing animation speed
                }
            });


        }

        function hideProcessing() {

//            $('#processingModal').modal('hide')

            $.noty.closeAll();

        }


        function showDataTable() {

            var districtId = $("#fvDistrctId").val();


            if (districtId == 0)
                $('#showIndiaModal').modal('show');
            else
                $('#showSPModal').modal('show');


        }

        function prevMonth() {


            var defYear = Number($("#year").text());
            var defMonth = Number($("#month").val());

            if (defMonth == 0) {
                defMonth = 11;
                defYear--;
            } else {
                defMonth--;
            }
            $("#month").val(defMonth);
            $("#year").text(defYear);

            $("#monthStr").text(months[defMonth]);

            refreshMapView();

        }

        function nextMonth() {


            var defYear = Number($("#year").text());
            var defMonth = Number($("#month").val());

            if (defMonth == 11) {
                defMonth = 1;
                defYear++;
            } else {
                defMonth++;
            }

            $("#month").val(defMonth);
            $("#year").text(defYear);

            $("#monthStr").text(months[defMonth]);

            refreshMapView();

        }


        function fetchDistricts(stateId) {

            $.ajax({
                url: "/imap/mapData/getDistrictsForState",
                dataType: 'json',
                async: false,
                data: {
                    stateId: stateId,
                },
                success: function (data) {

                    var districts = []

                    districts.push({text: 'All', id: 0})

                    if (data.length > 0) {

                        for (var i = 0; i < data.length; i++) {

                            districts.push({text: data[i].distrctName, id: data[i].id})

                        }
                    }
                    $("#fvDistrctId").select2({

                        data: districts

                    });




                }
            });
        }

        function getStateData() {

            var stateId = ($("#fvStateId").val());

            if (stateId == 0) {

                $("#popSwitchWindow").hide();
                $("#popSwitch").bootstrapSwitch("disabled", true);
                $("#popFilter").prop("disabled", true);
                $('#popSlider').jRange('disable')

                $("#fvDistrctId").select2("val", 0);
                $("#fvDistrctId").prop("disabled", true);


            } else {

                fetchDistricts(stateId);
                $("#fvDistrctId").select2("val", 0);
                $("#fvDistrctId").prop("disabled", false);
            }

            refreshMapView();
        }


        function changeNoDistExcp(value) {


            if (value == 1) {
                $("#muncpExcp").prop("checked", false)
                $("#panchExcp").prop("checked", false)
            } else if (value == 2) {
                $("#corpExcp").prop("checked", false)
                $("#panchExcp").prop("checked", false)
            } else {
                $("#corpExcp").prop("checked", false)
                $("#muncpExcp").prop("checked", false)
            }
        }


        function salesSelectrChange() {


            var salesSelctr = $('input[name=salesSelctr]:checked').val();

            if (salesSelctr == "VO") {
                $('#salsVolSliderDiv').show();
                $('#salsValSliderDiv').hide();
            } else {
                $('#salsVolSliderDiv').hide();
                $('#salsValSliderDiv').show();
            }

            $("#rangeFilter").prop("checked", false);

            refreshMapView();
        }

        function salesSliderChange(value) {
            salesRangeVals = value;

            $("#rangeFilter").prop("checked", false);

        }

        function popSliderChange(value) {
            popRangeVals = value;
            $("#popFilter").prop("checked", false);
        }


        function getDistrictData() {

            var districtId = $("#fvDistrctId").val();

            if (districtId == 0) {

                $("#popSwitchWindow").hide();
                $("#popSwitch").bootstrapSwitch("disabled", true);
                $("#popFilter").prop("disabled", true);
                $('#popSlider').jRange('disable');

            } else {
                $("#popSwitchWindow").show();
                $("#popSwitch").bootstrapSwitch("disabled", false);
                $("#popFilter").prop("disabled", false);
                $('#popSlider').jRange('enable');
            }

            refreshMapView();

        }


        function exploreMap(value, color) {

            distrctColor = color;

            var stateId = $("#fvStateId").val();
            var districtId = $("#fvDistrctId").val();

            if (stateId == 0) {

                $("#fvStateId").select2("val", value);
                $("#fvDistrctId").prop("disabled", false);

                $.ajax({
                    url: "/imap/mapData/getDistrictsForState",
                    dataType: 'json',
                    async: false,
                    data: {
                        stateId: value,
                    },
                    success: function (data) {

                        var districts = []

                        districts.push({text: 'All', id: 0})

                        if (data.length > 0) {

                            for (var i = 0; i < data.length; i++) {

                                districts.push({text: data[i].distrctName, id: data[i].id})

                            }
                        }
                        $("#fvDistrctId").select2({

                            data: districts

                        });

                        $("#fvDistrctId").select2("val", 0);


                    }
                });
                refreshMapView();
                return true;
            }

            if (districtId == 0) {

                $("#fvDistrctId").select2("val", value);

                $("#popSwitchWindow").show();
                $("#popSwitch").bootstrapSwitch("toggleDisabled", true, true);
                $("#popFilter").prop("disabled", false);
                $('#popSlider').jRange('enable');

                refreshMapView();
                return true;
            }
        }

        function rollUpExcp(level) {

            if (level == 1) { // Means roll up to national level

                $("#fvDistrctId").val(0)
                $("#fvStateId").val(0)

                $("#fvDistrctId").prop("disabled", false);
                $("#popSwitch").bootstrapSwitch("toggleDisabled", true, true);
                $("#popFilter").prop("disabled", false);
                $('#popSlider').jRange('enable');


            } else if (level == 2) { // Means roll up to district level


                $("#fvDistrctId").val(0)
                $("#popSwitch").bootstrapSwitch("toggleDisabled", true, true);
                $("#popFilter").prop("disabled", false);
                $('#popSlider').jRange('enable');

            }

            plotNoDistributorException();

        }

        function exploreExcp(value) {

            var stateId = $("#fvStateId").val();
            var districtId = $("#fvDistrctId").val();

            if (stateId == 0) {

                $("#fvStateId").select2("val", value);
                $("#fvDistrctId").prop("disabled", false);
                plotNoDistributorException();
                return true;
            }

            if (districtId == 0) {
                $("#fvDistrctId").select2("val", value);

                $("#popSwitch").bootstrapSwitch("toggleDisabled", true, true);
                $("#popFilter").prop("disabled", false);
                $('#popSlider').jRange('enable');
                plotNoDistributorException();
                return true;
            }
            //alert(value);
        }

        function initialize() {

            var bangalore = {lat: 12.97, lng: 77.59};

        }


        function showException() {

            var excpType1 = ($("#excpType1").is(":checked"));
            var excpType2 = ($("#excpType2").is(":checked"));

            var corpExcp = ($("#corpExcp").is(":checked"));
            var muncpExcp = ($("#muncpExcp").is(":checked"));
            var panchExcp = ($("#panchExcp").is(":checked"));


            if (excpType2 == true) {

                if (corpExcp == false && muncpExcp == false && panchExcp == false) {

                    alert("Please select at least a catregory")
                    return false;
                }

                plotNoDistributorException();

                hideExcepWindow("Distribution Expansion");
            }
        }

        function showExcepWindow() {

            $("#exceptionWindow").show();
            $("#excepSummary").prop("readonly", "readonly");


        }

        function hideExcepWindow(value) {

            var summary = "Exceptions";

            if ($("#excepSummary").val() == "Exceptions" && value != '') {
                summary = value;
                $("#excepSummary").val(summary);
            }

            $("#exceptionWindow").hide();

            $("#excepSummary").removeProp("readonly");


        }

        function zoomMap() {

            if ($("#mapDiv").hasClass('fullscreen') == false) {

                $("#mapDiv").attr("class", "col-md-12");
                $('#googleMap').css("height", "800px");
                $("#mapDiv").addClass('fullscreen');
                $("#mainDivContainer").hide();
                $("#appLogoContainer").hide();
                $("#breadcrumb").hide();
                $("#dataDiv").hide();
                viewMode = 'F'; // Full Screen mode

                $("#handle").html("<i class='fa fa-search-minus'></i> Back to Normal View");

            } else {

                $("#handle").html("<i class='fa fa-search-plus'></i> View Fullscreen");
                $("#mapDiv").attr("class", "col-md-6");
                $('#googleMap').css("height", "500px");
                viewMode = 'N'; // Normal Screen Mode
                $("#mainDivContainer").show();
                $("#appLogoContainer").show();
                $("#breadcrumb").show();
                $("#dataDiv").show()
            }

            var currCenter = new google.maps.LatLng(23.259933, 77.412615);
            google.maps.event.trigger(map, 'resize')
            map.setCenter(currCenter);
        }

        function getDistrictView(id) {

            var url = "/imap/mapData/districtView/" + id + "?view=" + viewMode;
            window.location.href = url;
        }


        function plotNoDistributorException() {


            var stateId = $("#fvStateId").val();
            var districtId = $("#fvDistrctId").val();
            var townId = $("#fvTownId").val();


            var townType = 1;


            var corpExcp = ($("#corpExcp").is(":checked"));
            var muncpExcp = ($("#muncpExcp").is(":checked"));
            var panchExcp = ($("#panchExcp").is(":checked"));


            if (corpExcp == true)
                townType = 1;
            else if (muncpExcp == true)
                townType = 2;
            else
                townType = 3;


            $.ajax({
                url: "/imap/dataException/plotNoDistributorException",
                dataType: 'json',
                async: false,
                data: {
                    stateId: stateId,
                    districtId: districtId,
                    townId: townId,
                    townType: townType

                },
                success: function (data) {



                    for (var i = 0; i < markers.length; i++) {
                        var marker = markers[i];
                        marker.setMap(null);
                    }

                    for (var i = 0; i < boundryGons.length; i++) {
                        var boundryGon = boundryGons[i];
                        boundryGon.setMap(null);
                    }

                    boundryGons = [];


                    for (var i = 0; i < circles.length; i++) {
                        var circle = circles[i];
                        circle.setMap(null);
                    }

                    var currCenter = new google.maps.LatLng(data.latVal, data.lngVal);
                    google.maps.event.trigger(map, 'resize')
                    map.setCenter(currCenter);

                    map.setZoom(data.zoomVal);


                    for (var i = 0; i < data.rows.length; i++) {

                        var markerPos = new google.maps.LatLng(data.rows[i][2], data.rows[i][3]);

                        var marker = new google.maps.Marker({
                            position: markerPos,
                            map: map,
                            title: data.rows[i][1],
                            icon: {
                                path: google.maps.SymbolPath.CIRCLE,
                                scale: data.pointSize,
                                strokeColor: data.rows[i][4],
                            },
                        });

                        markers.push(marker);

                        var contentString = "";

                        if (districtId == 0) {
                            contentString = '<div id="content">'
                                    + '<h3><small>' + data.rows[i][1] + '</small></h3>'
                                    + '<table id="summaryList" style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">'
                                    + '<tr> <td style="width:150px;"> Total ' + data.rows[i][7] + ' </td> <td align="right" style="color: blue; font-weight: bold"> <label >' + data.rows[i][5] + '</label> </td> </tr>'
                                    + '<tr> <td style="width:150px;"> Without distributor </td> <td align="right" style="color: blue; font-weight: bold"> <label >' + data.rows[i][6] + '</label> </td> </tr>'

                            contentString += '</table>';

                            if (data.rows[i][5] != 0)
                                contentString += '<br><a class="btn btn-info btn-xs" href="#" onclick="exploreExcp( ' + data.rows[i][0] + ')">Explore</a>';

                            if (stateId != 0) {
                                contentString += '&nbsp;<a class="btn btn-warning btn-xs" href="#" onclick="rollUpExcp(1)">Back</a>';
                            }
                            contentString += '</div>';
                        } else {

                            contentString = '<div id="content">'
                                    + '<h3><small>' + data.rows[i][1] + ' (' + data.rows[i][7] + ')</small></h3>'
                                    + '<table id="summaryList" style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">'
                                    + '<tr> <td style="background-color: lightskyblue;"> Stockistpoint  </td> <td style="background-color: lightskyblue;">Type </td> </tr>';


                            var stcstpntList = data.rows[i][8];

                            for (var k = 0; k < stcstpntList.length; k++) {
                                contentString += '<tr> <td>' + stcstpntList[k][1] + '</td> <td> ' + stcstpntList[k][2] + '</td> </tr>';
                            }

                            contentString += '</table>';


                            if (stcstpntList.length == 0) {
                                contentString += '<font color="red"> No stockistpoints found.</font><br> ';
                            }


                            contentString += '<br><a class="btn btn-warning btn-xs" href="#" onclick="rollUpExcp(2)">Back</a>';
                            contentString += '</div>';
                        }


                        infoWindow = new google.maps.InfoWindow({
                            content: contentString
                        });

                        google.maps.event.addListener(marker, 'click', function (pointer, bubble) {

                            return function () {

                                if (prev_infoWindow) {
                                    prev_infoWindow.close();
                                }

                                for (var i = 0; i < subMarkers.length; i++) {
                                    subMarkers[i].setMap(null);
                                }
                                subMarkers = [];

                                prev_infoWindow = bubble;
                                bubble.open(map, pointer);
                            };
                        }(marker, infoWindow));

                    }

                }
            });


        }

        function refreshMapView() {


            alert('Hello');


            if (isFilterHidden == false)
                toggleFilter();

            showProcessing();


            $("#excepSummary").val('Exceptions');
            hideExcepWindow('');

            var popSwitch = ($("#popSwitch").is(":checked"));

            var stateId = $("#fvStateId").val();
            var districtId = $("#fvDistrctId").val();
            var townId = $("#fvTownId").val();
           // var brandId = $('#fvBrandId').multipleSelect('getSelects');


            var brandId = ($("#fvBrandId" + id).text()).split(",");

            var monthId = $('#month').val();
            var yearId = $('#year').text();

            var stockistN = ($("#stockistN").is(":checked"));
            var stockistS = ($("#stockistS").is(":checked"));
            var stockistM = ($("#stockistM").is(":checked"));


            var townType1 = ($("#townType1").is(":checked"));
            var townType2 = ($("#townType2").is(":checked"));
            var townType3 = ($("#townType3").is(":checked"));

            var townTypes = "";

            var radius = $('#spRadius').val();


            if (townType1 == true) {
                townTypes = "1";
            }

            if (townType2 == true) {

                if (townTypes != "") {
                    townTypes += "," + "2"
                } else {
                    townTypes = "2";
                }
            }

            if (townType3 == true) {

                if (townTypes != "") {
                    townTypes += "," + "3"
                } else {
                    townTypes = "3";
                }
            }


            var rurMpvs = "", urbMpvs = "";

            var rurA = ($("#rurA").is(":checked"));
            var rurB = ($("#rurB").is(":checked"));
            var rurC = ($("#rurC").is(":checked"));

            if (rurA == true) {
                rurMpvs = "'A'";
            }

            if (rurB == true) {

                if (rurMpvs != "") {
                    rurMpvs += "," + "'B'"
                } else {
                    rurMpvs = "'B'";
                }
            }

            if (rurC == true) {

                if (rurMpvs != "") {
                    rurMpvs += "," + "'C'"
                } else {
                    rurMpvs = "'C'";
                }
            }


            var urbA = ($("#urbA").is(":checked"));
            var urbB = ($("#urbB").is(":checked"));
            var urbC = ($("#urbC").is(":checked"));

            if (urbA == true) {
                urbMpvs = "'A'";
            }

            if (urbB == true) {

                if (urbMpvs != "") {
                    urbMpvs += "," + "'B'"
                } else {
                    urbMpvs = "'B'";
                }
            }

            if (urbC == true) {

                if (urbMpvs != "") {
                    urbMpvs += "," + "'C'"
                } else {
                    urbMpvs = "'C'";
                }
            }


            var applySalesRange = $("#rangeFilter").is(":checked");
            var applyPopRange = $("#popFilter").is(":checked");

            var stockistVals = "";

            if (stockistN == true) {

                stockistVals = "4";
            }

            if (stockistS == true) {

                if (stockistVals != "") {
                    stockistVals += "," + "3,5"
                } else {
                    stockistVals = "3,5";
                }
            }

            if (stockistM == true) {

                if (stockistVals != "") {
                    stockistVals += "," + "1,2"
                } else {
                    stockistVals = "1,2";
                }
            }

            var salesSelctr = $('input[name=salesSelctr]:checked').val();

            if (stockistVals == "") {
                alert("Please select atlest one stockist point type")
                return false;
            }

            var url = "";

            if (districtId == 0) {
                url = "/imap/mapData/getMapDataForStateDistrct";
            } else {
                url = "/imap/mapData/getMapDataForStockists";
            }


            alert(brandId);

            $.ajax({
                url: url,
                dataType: 'json',
                async: true,
                data: {
                    stateId: stateId,
                    districtId: districtId,
                    townId: townId,
                    brandId: brandId,
                    stockistVals: stockistVals,
                    monthId: monthId,
                    yearId: yearId,
                    salesSelctr: salesSelctr,
                    salesRangeVals: salesRangeVals,
                    popRangeVals: popRangeVals,
                    applySalesRange: applySalesRange,
                    rurMpvs: rurMpvs,
                    urbMpvs: urbMpvs,
                    townTypes: townTypes
                },
                success: function (data) {

                    var stcstId = 0;

                    $("#avgSalesPerPop").text(data.avgSalePerPop);
                    $("#totSales").text(data.totalSales);
                    $("#totPop").text(data.totalPop);

                    for (var i = 0; i < boundryGons.length; i++) {
                        var boundryGon = boundryGons[i];
                        boundryGon.setMap(null);
                    }
                    boundryGons = [];


                    for (var i = 0; i < markers.length; i++) {
                        var marker = markers[i];
                        marker.setMap(null);
                    }
                    ;

                    markers = [];

                    for (var i = 0; i < circles.length; i++) {
                        var circle = circles[i];
                        circle.setMap(null);
                    }

                    circles = [];

//                    alert("Lat = " + data.latVal + " Lang = " + data.lngVal);

                    var currCenter = new google.maps.LatLng(data.latVal, data.lngVal);
                    google.maps.event.trigger(map, 'resize')
                    map.setCenter(currCenter);

                    map.setZoom(data.zoomVal);

                    var prevLatVal = 99999;
                    var prevLngVal = 99999;

                    var contentString = "";
                    var innerString = "";

                    var infoWindow = "";


                    var spotColor = "";
                    var spotScale = "";
                    var spotCount = 0;


                    tableData = data.rows;


                    if (oTable) {
                        oTable.clear().draw();
                        oTable.rows.add(tableData); // Add new data
                        oTable.columns.adjust().draw(); // Redraw the DataTable
                    }


                    if (oTable1 && districtId != 0) {
                        oTable1.clear().draw();
                        oTable1.rows.add(tableData); // Add new data
                        oTable1.columns.adjust().draw(); // Redraw the DataTable
                    }


                    var image = "";


                    $('#salesList tr').remove();

                    if (stateId == 0) {
                        $("#salesList").last().append('<tr>' +
                                '<td style="width:20px;color: darkblue;">#</td>' +
                                '<td style="color:darkblue;">State</td>' +
                                '<td align="center" style="color:darkblue;">Value</td>' +
                                '</tr>');

                        $("#salesDtlsTitle").text("Sales Index - All over India");
                        $("#lblMapTitle").text("India Map");


                    } else if (districtId == 0) {

                        $("#salesList").last().append('<tr>' +
                                '<td style="width:20px;color: darkblue;">#</td>' +
                                '<td style="color:darkblue;">District</td>' +
                                '<td align="center" style="color:darkblue;">Value</td>' +
                                '</tr>');

                        var stateName = $("#fvStateId").select2('data').text;

                        $("#salesDtlsTitle").text("Sales Index - " + stateName + " State");
                        $("#lblMapTitle").text(stateName + " State Map");
                    } else {


                        $("#salesList").last().append('<tr>' +
                                '<td style="width:20px;color: darkblue;">#</td>' +
                                '<td style="color:darkblue;">Stockist</td>' +
                                '<td align="center" style="color:darkblue;">Scope</td>' +
                                '</tr>');

                        var districtName = $("#fvDistrctId").select2('data').text;

                        $("#salesDtlsTitle").text("Sales Index - " + districtName + " District");
                        $("#lblMapTitle").text(districtName + " District Map");

                    }

                    for (var i = 0; i < data.rows.length; i++) {

                        if (districtId != 0) {

                            if (data.rows[i][11] == 1 || data.rows[i][11] == 2) { // Mass Stockist && Mass Stockist 2

                                image = '${resource(dir: 'images', file: 'square.png')}';

                            } else if (data.rows[i][11] == 4) { // Stockist

                                image = '${resource(dir: 'images', file: 'triangle.png')}';
                            } else if (data.rows[i][11] == 3 || data.rows[i][11] == 5) {

                                image = '${resource(dir: 'images', file: 'circle.png')}';

                            }


                            $("#salesList").last().append('<tr style="color:white; background-color:' + data.rows[i][5] + '">' +
                                    '<td>' + (i + 1) + '</td>' +
                                    '<td>' + data.rows[i][1] + '</td>' +
                                    '<td align="right" style="font-weight: bold">' + data.rows[i][8] + '</td>' +
                                    '</tr>');

                            // For the first time, just add the details to contentString and continue

                            if (prevLatVal == 99999 &&
                                    prevLngVal == 99999) {

                                prevLatVal = data.rows[i][2];
                                prevLngVal = data.rows[i][3];
                                spotScale = data.rows[i][6];
                                spotColor = data.rows[i][5];
                                spotCount = 1;


                                innerString = '<div id="content">'
                                        + '<h4><small style="color: ' + data.rows[i][5] + '">' + data.rows[i][1] + '</small></h4>'
                                        + '<table id="summaryList" style="background-color:' + data.rows[i][5] + '; color: white; style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">'
                                        + '<tr> <td> Total District Population </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.totalPop + '</label> </td> </tr>'
                                        + '<tr> <td> Total Sales </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][4] + '</label> </td> </tr>'
                                        + '<tr> <td> Per Capita Sales(Total Scope) </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][8] + '</label> </td> </tr>'
                                        + '<tr> <td> Total Scope </td> <td align="right" style="color:white;"> <label>' + data.rows[i][9]
                                        + '<tr> <td> Per Capita Sales (Covered Scope) </td> <td align="right" style="color:white;"> <label>' + data.rows[i][14]
                                        + '<tr> <td> Covered Scope </td> <td align="right" style="color:white;"> <label>' + data.rows[i][15]
                                        + '</table>';

                                innerString += '</div>';


                                if (data.rows[i][11] == 5) {

                                    var subList = data.rows[i][12];

                                    var subListTable = "<br> Sub Stockists ";

                                    subListTable += '<table id="subList" style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">';

                                    for (var j = 0; j < subList.length; j++) {

                                        subListTable += '<tr> <td>' + (j + 1) + '</td>';
                                        subListTable += '<td>' + subList[j] + '</td> </tr>';

                                    }

                                    subListTable += '</table>';


                                    innerString += "" + subListTable

                                }


                                contentString = innerString;


//                                alert("First record for " + data.rows[i][1]);

                                // If there are only one record, mark it here itself.
                                if (data.rows.length == 1) {

                                    var markerPos = new google.maps.LatLng(prevLatVal, prevLngVal);
                                    var marker = new google.maps.Marker({
                                        position: markerPos,
                                        map: map,
                                        title: "Stockist Points",
                                        icon: image
                                    });


                                    if (radius > 0) {

                                        var circle = new google.maps.Circle({
                                            map: map,
                                            radius: radius * 1000,    // 5 miles in metres
                                            fillColor: 'green'
                                        });

                                        circle.bindTo('center', marker, 'position');
                                        circles.push(circle);
                                    }

                                    markers.push(marker);

                                    infoWindow = new google.maps.InfoWindow({
                                        content: contentString
                                    });

                                    google.maps.event.addListener(marker, 'click', function (pointer, bubble) {

                                        return function () {

                                            if (prev_infoWindow) {
                                                prev_infoWindow.close();
                                            }

                                            for (var i = 0; i < subMarkers.length; i++) {
                                                subMarkers[i].setMap(null);
                                            }
                                            subMarkers = [];

                                            prev_infoWindow = bubble;
                                            bubble.open(map, pointer);
                                        };
                                    }(marker, infoWindow));

                                }

                                continue;

                            }

                            // Next time onwards, check if the latitude and longitude repeats.
                            // If repeats, then simply add to contentString and continue
                            if (prevLatVal == data.rows[i][2] &&
                                    prevLngVal == data.rows[i][3]) {

                                spotCount++;

                                innerString = '<div id="content">'
                                        + '<h4><small style="color: ' + data.rows[i][5] + '">' + data.rows[i][1] + '</small></h4>'
                                        + '<table id="summaryList" style="background-color:' + data.rows[i][5] + '; color: white; style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">'
                                        + '<tr> <td> Total District Population </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.totalPop + '</label> </td> </tr>'
                                        + '<tr> <td> Total Sales </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][4] + '</label> </td> </tr>'
                                        + '<tr> <td> Per Capita Sales(Total Scope) </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][8] + '</label> </td> </tr>'
                                        + '<tr> <td> Total Scope </td> <td align="right" style="color:white;"> <label>' + data.rows[i][9]
                                        + '<tr> <td> Per Capita Sales (Per Capita Sales (Covered Scope)) </td> <td align="right" style="color:white;"> <label>' + data.rows[i][14]
                                        + '<tr> <td> Covered Scope </td> <td align="right" style="color:white;"> <label>' + data.rows[i][15]
                                        + '</table>';

                                innerString += '</div>';

                                if (data.rows[i][11] == 5) {

                                    var subList = data.rows[i][12];

                                    var subListTable = "<br> Sub Stockists ";

                                    subListTable += '<table id="subList" style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">';

                                    for (var j = 0; j < subList.length; j++) {

                                        subListTable += '<tr> <td>' + (j + 1) + '</td>';
                                        subListTable += '<td>' + subList[j] + '</td> </tr>';

                                    }

                                    subListTable += '</table>';


                                    innerString += "" + subListTable

                                }

                                contentString += "<br><br>" + innerString;

//                                alert("Continuing with old contentString = " + contentString);

                                if (i < (data.rows.length - 1)) {
                                    continue;
                                }


                            } else {      // If the latitude and longitude different from previous one, mark it in map and proceed with next set


                                if (spotCount > 1) {

                                    spotScale = 9;
                                    spotColor = "#C705FC";
                                    image = '${resource(dir: 'images', file: 'star.png')}';
                                }

                                var markerPos = new google.maps.LatLng(prevLatVal, prevLngVal);
                                var marker = new google.maps.Marker({
                                    position: markerPos,
                                    map: map,
                                    title: "Stockist Points",
                                    icon: image
                                });


                                if (radius > 0) {

                                    var circle = new google.maps.Circle({
                                        map: map,
                                        radius: radius * 1000,    // 5 miles in metres
                                        fillColor: 'green'
                                    });

                                    circle.bindTo('center', marker, 'position');
                                    circles.push(circle);
                                }

                                markers.push(marker);

                                infoWindow = new google.maps.InfoWindow({
                                    content: contentString
                                });


                                google.maps.event.addListener(marker, 'click', function (pointer, bubble) {

                                    return function () {

                                        if (prev_infoWindow) {
                                            prev_infoWindow.close();
                                        }

                                        for (var i = 0; i < subMarkers.length; i++) {
                                            subMarkers[i].setMap(null);
                                        }
                                        subMarkers = [];

                                        prev_infoWindow = bubble;
                                        bubble.open(map, pointer);
                                    };
                                }(marker, infoWindow));

                                prevLatVal = data.rows[i][2];
                                prevLngVal = data.rows[i][3];
                                spotScale = data.rows[i][6];
                                spotColor = data.rows[i][5];
                                spotCount = 1;


                                innerString = '<div id="content">'
                                        + '<h4><small style="color: ' + data.rows[i][5] + '">' + data.rows[i][1] + '</small></h4>'
                                        + '<table id="summaryList" style="background-color:' + data.rows[i][5] + '; color: white; style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">'
                                        + '<tr> <td> Total District Population </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.totalPop + '</label> </td> </tr>'
                                        + '<tr> <td> Total Sales </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][4] + '</label> </td> </tr>'
                                        + '<tr> <td> Per Capita Sales(Total Scope) </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][8] + '</label> </td> </tr>'
                                        + '<tr> <td> Total Scope </td> <td align="right" style="color:white;"> <label>' + data.rows[i][9]
                                        + '<tr> <td> Per Capita Sales (Covered Scope) </td> <td align="right" style="color:white;"> <label>' + data.rows[i][14]
                                        + '<tr> <td> Covered Scope </td> <td align="right" style="color:white;"> <label>' + data.rows[i][15]
                                        + '</table>';

                                innerString += '</div>';


                                if (data.rows[i][11] == 5) {

                                    var subList = data.rows[i][12];

                                    var subListTable = "<br> Sub Stockists ";

                                    subListTable += '<table id="subList" style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">';

                                    for (var j = 0; j < subList.length; j++) {

                                        subListTable += '<tr> <td>' + (j + 1) + '</td>';
                                        subListTable += '<td>' + subList[j] + '</td> </tr>';

                                    }

                                    subListTable += '</table>';


                                    innerString += "" + subListTable

                                }

                                contentString = innerString;

                                if (i < (data.rows.length - 1)) {
                                    continue;
                                }

                            }


                            if (spotCount > 1) {
                                spotScale = 9;
                                spotColor = "#C705FC";
                                image = '${resource(dir: 'images', file: 'star.png')}';
                            }


                            var markerPos = new google.maps.LatLng(prevLatVal, prevLngVal);
                            var marker = new google.maps.Marker({
                                position: markerPos,
                                map: map,
                                title: "Stockist Points",
                                icon: image
                            });

                            if (radius > 0) {

                                var circle = new google.maps.Circle({
                                    map: map,
                                    radius: radius * 1000,    // 5 miles in metres
                                    fillColor: 'green'
                                });

                                circle.bindTo('center', marker, 'position');
                                circles.push(circle);
                            }

                            markers.push(marker);

                            infoWindow = new google.maps.InfoWindow({
                                content: contentString
                            });


                            var triangleCoords = data.rows[i][17];

                            // Construct the polygon.
                            var boundryGon = new google.maps.Polygon({
                                paths: triangleCoords,
//                                    strokeColor: "black",
                                strokeColor: "black",
                                strokeOpacity: 0.4,
                                strokeWeight: 1,
                                fillColor: distrctColor,
                                fillOpacity: 0.5
                            });
                            boundryGon.setMap(map);


                            boundryGons.push(boundryGon);


                            google.maps.event.addListener(marker, 'click', function (pointer, bubble) {

                                return function () {

                                    if (prev_infoWindow) {
                                        prev_infoWindow.close();
                                    }

                                    for (var i = 0; i < subMarkers.length; i++) {
                                        subMarkers[i].setMap(null);
                                    }
                                    subMarkers = [];

                                    prev_infoWindow = bubble;
                                    bubble.open(map, pointer);
                                };
                            }(marker, infoWindow));

                        } else { // This means district id is 0

                            var functionStr = "exploreMap(" + data.rows[i][0] + ",'" + data.rows[i][5] + "')";

                            $("#salesList").last().append('<tr style="color:white; background-color:' + data.rows[i][5] + '">' +
                                    '<td>' + (i + 1) + '</td>' +
                                    '<td><a style="color:white;" href="#" onclick="' + functionStr + ';">' + data.rows[i][1] + '</a></td>' +
                                    '<td align="right" style="font-weight: bold">' + data.rows[i][8] + '</td>' +
                                    '</tr>');

                            var markerPos = new google.maps.LatLng(data.rows[i][2], data.rows[i][3]);

                            if (districtId == 0) {

                                contentString = '<div id="content">'
                                        + '<h3><small><font style="color: ' + data.rows[i][5] + '">' + data.rows[i][1] + '</font></small></h3>'
                                        + '<table id="summaryList" style="background-color:' + data.rows[i][5] + '; color: white; width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">'
                                        + '<tr> <td> Total Population </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][7] + '</label> </td> </tr>'
                                        + '<tr> <td> Total Sales </td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][4] + '</label> </td> </tr>'
                                        + '<tr> <td> Per Capita Sales(Total Scope)</td> <td align="right" style="color: white; font-weight: bold"> <label >' + data.rows[i][8] + '</label> </td> </tr>'
                                        + '<tr> <td> Per Capita Sales (Covered Scope) </td> <td align="right" style="color:white;"> <label>' + data.rows[i][16]
                                        + '<tr> <td> Stockists </td> <td align="right" style="color: white;"> <label> Stockist - ' + data.rows[i][11] + '<br>Super-Sub - ' + data.rows[i][10] + '<br>Mass - ' + data.rows[i][9] + '</label> </td> </tr>';


                                if (stateId != 0) {
                                    contentString += '<tr> <td> MPV </td> <td align="right" style="color: white;"> <label>Rural - ' + data.rows[i][12] + '<br>Urban - ' + data.rows[i][14] + '</label> </td> </tr>';
                                }


                                contentString += '</table>';


                                contentString += '<div align="right" style="width:100%;"><a class="btn btn-primary btn-xs" href="#" onclick="' + functionStr + '">Explore</a></div>';
                                contentString += '</div>';

                            }

                            infoWindow = new google.maps.InfoWindow({
                                content: contentString,
                                position: markerPos,
                                pixelOffset: 0
                            });

                            if (data.rows[i][17] != null) {

                                var triangleCoords = data.rows[i][17];

                                // Construct the polygon.
                                var boundryGon = new google.maps.Polygon({
                                    paths: triangleCoords,
                                    strokeColor: "black",
                                    strokeOpacity: 0.8,
                                    strokeWeight: 1,
                                    fillColor: data.rows[i][5],
                                    fillOpacity: 0.5
                                });
                                boundryGon.setMap(map);


                                boundryGons.push(boundryGon);


                                google.maps.event.addListener(boundryGon, 'click', function (pointer, bubble) {

                                    return function () {

                                        if (prev_infoWindow) {
                                            prev_infoWindow.close();
                                        }

                                        for (var i = 0; i < subMarkers.length; i++) {
                                            subMarkers[i].setMap(null);
                                        }
                                        subMarkers = [];

                                        prev_infoWindow = bubble;
                                        bubble.open(map, pointer);
                                    };
                                }(boundryGon, infoWindow));


//                                google.maps.event.addListener(boundryGon, 'mouseover', function (pointer, bubble) {
//
//                                    return function () {
//
//                                        if (prev_infoWindow) {
//                                            prev_infoWindow.close();
//                                        }
//
//                                        for (var i = 0; i < subMarkers.length; i++) {
//                                            subMarkers[i].setMap(null);
//                                        }
//                                        subMarkers = [];
//
//                                        prev_infoWindow = bubble;
//                                        bubble.open(map, pointer);
//                                    };
//                                }(boundryGon, infoWindow));


                            } else {


                                var marker = new google.maps.Marker({
                                    position: markerPos,
                                    map: map,
                                    title: data.rows[i][1],
                                    icon: {
                                        path: google.maps.SymbolPath.CIRCLE,
                                        scale: data.rows[i][6],
                                        strokeColor: data.rows[i][5],

                                    },
                                });

                                markers.push(marker);

                                google.maps.event.addListener(marker, 'click', function (pointer, bubble) {

                                    return function () {

                                        if (prev_infoWindow) {
                                            prev_infoWindow.close();
                                        }

                                        for (var i = 0; i < subMarkers.length; i++) {
                                            subMarkers[i].setMap(null);
                                        }
                                        subMarkers = [];

                                        prev_infoWindow = bubble;
                                        bubble.open(map, pointer);
                                    };
                                }(marker, infoWindow));
                            }
                        }
                    }


                    if (townTypes != "" && stateId != 0) {

                        $.ajax({
                            url: "/imap/mapData/getCorpMapData",
                            dataType: 'json',
                            async: true,
                            data: {
                                stateId:stateId,
                                districtId: districtId,
                                townTypes: townTypes
                            },
                            success: function (data) {

                                for (var i = 0; i < data.rows.length; i++) {

                                    var markerPos = new google.maps.LatLng(data.rows[i][2], data.rows[i][3]);

                                    var image;

                                    if(data.rows[i][5] == 1) {
                                        image = '${resource(dir: 'images', file: 'corporation.png')}';
                                    } else {
                                        image = '${resource(dir: 'images', file: 'muncipality.png')}';
                                    }
                                    var marker = new google.maps.Marker({
                                        position: markerPos,
                                        map: map,
                                        title: data.rows[i][1],
                                        icon: image
                                    });


                                    markers.push(marker);

                                    var contentString = '<div id="content">'
                                            + '<h3><small>' + data.rows[i][1] + '</small></h3>'
                                            + '<table style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">'
                                            + '<tr> <td> Total Population </td> <td align="right" style="color: blue; font-weight: bold"> <label >' + data.rows[i][4] + '</label> </td> </tr>'
                                            + '</table>';

                                    contentString += '</div>';

                                    var infoWindow = new google.maps.InfoWindow({
                                        content: contentString
                                    });


                                    google.maps.event.addListener(marker, 'click', function (pointer, bubble) {

                                        stcstId = data.rows[i][3];

                                        return function () {

                                            if (prev_infoWindow) {
                                                prev_infoWindow.close();
                                            }

                                            for (var i = 0; i < subMarkers.length; i++) {
                                                subMarkers[i].setMap(null);
                                            }
                                            subMarkers = [];

                                            prev_infoWindow = bubble;
                                            bubble.open(map, pointer);
                                        };
                                    }(marker, infoWindow));


                                }


                            }
                        })
                    }
                    if (districtId != 0 && popSwitch == true) {

                        $.ajax({
                            url: "/imap/mapData/getTownMapData",
                            dataType: 'json',
                            async: true,
                            data: {
                                districtId: districtId,
                                popRangeVals: popRangeVals,
                                applyPopRange: applyPopRange,
                                townTypes: townTypes
                            },
                            success: function (data) {

                                for (var i = 0; i < data.rows.length; i++) {

                                    var markerPos = new google.maps.LatLng(data.rows[i][2], data.rows[i][3]);

                                    var marker = new google.maps.Marker({
                                        position: markerPos,
                                        map: map,
                                        title: data.rows[i][1],
                                        icon: {
                                            path: google.maps.SymbolPath.CIRCLE,
                                            scale: 4,
                                            strokeColor: "lightgreen",
                                        },
                                    });

                                    markers.push(marker);

                                    var contentString = '<div id="content">'
                                            + '<h3><small>' + data.rows[i][1] + '</small></h3>'
                                            + '<table style="width: 100%; font-size: 10px;" class="table table-condensed table-bordered" cellspacing="0">'
                                            + '<tr> <td> Total Population </td> <td align="right" style="color: blue; font-weight: bold"> <label >' + data.rows[i][4] + '</label> </td> </tr>'
                                            + '</table>';

                                    contentString += '</div>';

                                    var infoWindow = new google.maps.InfoWindow({
                                        content: contentString
                                    });


                                    google.maps.event.addListener(marker, 'click', function (pointer, bubble) {

                                        stcstId = data.rows[i][3];

                                        return function () {

                                            if (prev_infoWindow) {
                                                prev_infoWindow.close();
                                            }

                                            for (var i = 0; i < subMarkers.length; i++) {
                                                subMarkers[i].setMap(null);
                                            }
                                            subMarkers = [];

                                            prev_infoWindow = bubble;
                                            bubble.open(map, pointer);
                                        };
                                    }(marker, infoWindow));


                                }


                            }
                        })
                    }
                    hideProcessing();
                }

            });






        }


        $(document)
                .ready(
                function () {


                    //showProcessing();

                    var currDate = new Date();
                    var defYear = currDate.getYear() + 1900;
                    var defMonth = currDate.getMonth();

                    if (defMonth == 0) {
                        defMonth = 12;
                        defYear = 1;
                    } else {
                        defMonth--;
                        defMonth--;
                        defMonth--;
                    }

                    $("#month").val(defMonth);
                    $("#year").text(defYear);

                    $("#monthStr").text(months[defMonth]);

                    $('#popSlider').jRange('disable');

                    $("#frmHeading").hide();

                    var styles = [{

                        elementType: 'labels',
                        stylers: [{visibility: 'off'}]
                    }
                    ];

                    if (('${params.view}') != "")
                        viewMode = '${params.view}';

                    var styledMap = new google.maps.StyledMapType(styles);

                    var mapProp = {
                        center: new google.maps.LatLng(23.259933, 77.412615),
                        zoom: 4,
                        // mapTypeId: google.maps.MapTypeId.TERRAIN,
                        disableDefaultUI: true,
                        scrollwheel: true,
                        navigationControl: true,
                        draggable: true,
                        disableDoubleClickZoom: true,
//                        mapTypeControlOptions: {
//                            mapTypeIds: ['map_style']
//                        },
//                        mapTypeId: 'map_style'

                    };
                    map = new google.maps.Map(document.getElementById("googleMap"), mapProp);
                    google.maps.event.addDomListener(window, 'load', initialize);
                    map.mapTypes.set('map_style', styledMap);


                    var imageBounds = {
                        north: 95.9,
                        south: -9.2,
                        east: 190.3,
                        west: 10.4
                    };


                    %{--var historicalOverlay = new google.maps.GroundOverlay(--}%
                    %{--'${resource(dir: 'images', file: 'bluebg.jpg')}',--}%
                    %{--imageBounds);--}%
                    %{--historicalOverlay.setMap(map);--}%

                    refreshMapView();

//                    if (viewMode == 'F')
//                        zoomMap();


                    var buttonCommon = {
                        exportOptions: {
                            format: {
                                body: function (data, column, row) {
                                    // Strip $ from salary column to make it numeric
                                    return column === 5 ?
                                            data.replace(/[$,]/g, '') :
                                            data;
                                }
                            }
                        }
                    };


                    oTable = $('#totalList').DataTable({
                        data: tableData,
                        "order": [[8, "desc"]],
                        buttons: [
                            $.extend(true, {}, buttonCommon, {
                                extend: 'copyHtml5'
                            }),
                            $.extend(true, {}, buttonCommon, {
                                extend: 'excelHtml5'
                            }),
                            $.extend(true, {}, buttonCommon, {
                                extend: 'pdfHtml5'
                            })
                        ],

                        "columnDefs": [
                            {
                                "targets": [2],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [3],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [4],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;'>"
                                            + data
                                            + "</div>";
                                }

                            },
                            {
                                "targets": [5],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [6],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [7],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;'>"
                                            + data
                                            + "</div>";
                                }

                            },
                            {
                                "targets": [8],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;color:" + row[5] + ";'>"
                                            + data
                                            + "</div>";
                                }

                            },
                            {
                                "targets": [9],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;'>"
                                            + data
                                            + "</div>";
                                }

                            },
                            {
                                "targets": [10],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;'>"
                                            + data
                                            + "</div>";
                                }

                            },
                            {
                                "targets": [11],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;'>"
                                            + data
                                            + "</div>";
                                }

                            },

                            {
                                "targets": [12],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [13],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [14],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [15],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [16],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;color:" + row[5] + ";'>"
                                            + data
                                            + "</div>";
                                }

                            },
                        ]
                    });

                    oTable.on('order.dt search.dt', function () {
                        var i = 0;

                        oTable.column(0, {search: 'applied', order: 'applied'}).nodes().each(function (cell, i) {
                            cell.innerHTML = i + 1;
                        });
                    }).draw();


                    oTable1 = $('#SPList').DataTable({
                        data: tableData,
                        "order": [[8, "desc"]],
                        buttons: [
                            $.extend(true, {}, buttonCommon, {
                                extend: 'copyHtml5'
                            }),
                            $.extend(true, {}, buttonCommon, {
                                extend: 'excelHtml5'
                            }),
                            $.extend(true, {}, buttonCommon, {
                                extend: 'pdfHtml5'
                            })
                        ],

                        "columnDefs": [
                            {
                                "targets": [2],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [3],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [4],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;'>"
                                            + data
                                            + "</div>";
                                }

                            },
                            {
                                "targets": [5],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [6],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [7],
                                "visible": false,
                                "searchable": false

                            },
                            {
                                "targets": [8],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;color:" + row[5] + ";'>"
                                            + data
                                            + "</div>";
                                }

                            },
                            {
                                "targets": [9],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;'>"
                                            + data
                                            + "</div>";
                                }

                            },
                            {
                                "targets": [10],
                                "visible": false,
                                "searchable": false

                            },
                            {
                                "targets": [11],
                                "visible": false,
                                "searchable": false

                            },

                            {
                                "targets": [12],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [13],
                                "visible": false,
                                "searchable": false
                            },
                            {
                                "targets": [14],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;color:" + row[5] + ";'>"
                                            + data
                                            + "</div>";
                                }
                            },
                            {
                                "targets": [15],
                                "render": function (data, type,
                                                    row, meta) {
                                    return "<div style='text-align: right;color:" + row[5] + ";'>"
                                            + data
                                            + "</div>";
                                }
                            },
                            {
                                "targets": [16],
                                "visible": false,
                                "searchable": false

                            },
                        ]
                    });

                    oTable1.on('order.dt search.dt', function () {
                        var i = 0;

                        oTable1.column(0, {search: 'applied', order: 'applied'}).nodes().each(function (cell, i) {
                            cell.innerHTML = i + 1;
                        });
                    }).draw();




                });
    </script>
</head>

<body id="indexBody">

%{--<div id="breadcrumb" class="container-fluid row">--}%
%{--<div class="col-md-12" align="left">--}%

%{--<ol class="breadcrumb">--}%
%{--<li>Population Plot - India</li>--}%
%{--</ol>--}%
%{--</div>--}%
%{--</div>--}%



<!-- Define modal for Editing new Account Sub Type Record -->
<div id="showIndiaModal" role="dialog" align="center"
     aria-hidden="true" class="modal fade" tabindex="-1">

    <!-- Use Bootstrap Modal and set custome Width and Height for the Modal -->
    <div class="modal-dialog" style="width: 900px; height: 900px;">

        <!-- Define modal-content -->
        <div class="modal-content ">

            <!-- Define modal-header -->
            <div class="modal-header">
                <!-- Add Close Button -->
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                </button>
                <h4>
                    Data View
                    <label style="color: #0C12B0;">
                        <small>States</small>
                    </label>
                </h4>
            </div>
            <!-- End Of modal-header -->

            <!-- Define modal-Body -->
            <div class="modal-body" style="width: 100%; height: 100%;"
                 align="left">

                <div class="row">
                    <div class="col-md-12 col-xs-12 col-sm-12 col-lg-12">

                        <!-- jqGrid Component Table -->
                        <table id="totalList" style="width: 100%;font-size: 12px;"
                               class="table table-condensed table-bordered  table-hover" cellspacing="0">

                            <thead>
                            <th></th>
                            <th style="width:200px;">State Name</th>
                            <th>Latitude</th>
                            <th>Longitude</th>
                            <th>Total Sales</th>
                            <th>Color</th>
                            <th>Size</th>
                            <th>Population</th>
                            <th>Total Scope</th>
                            <th>Mass</th>
                            <th>Super</th>
                            <th>Stockists</th>
                            <th>DUMMY</th>
                            <th>DUMMY</th>
                            <th>DUMMY</th>
                            <th>DUMMY</th>
                            <th>Covered Scope</th>

                            </thead>
                        </table>

                        <div style="float: left; height: 15px; width: 100%;" align="left"></div>

                    </div>
                </div>

                <!-- Actual Form for Data Submitting -->

            </div>

            <!-- End Of modal-body -->

            <!-- Define modal-Footer -->
            <div class="modal-footer" style="width: 100%;" align="center">

                <div style="float: left; width: 100%;" align="center">
                    <label style="color: #0C12B0;">
                        <small>Click close button or press 'Esc' button to close
                        this window</small>
                    </label>
                </div>

            </div>
            <!-- End Of modal-footer -->
        </div>

    </div>

    <!-- End Of modal-content -->

</div>
<!-- End Of modal-dialog -->

<!-- End Of showIndiaModal -->



<!-- Define modal for Editing new Account Sub Type Record -->
<div id="showSPModal" role="dialog" align="center"
     aria-hidden="true" class="modal fade" tabindex="-1">

    <!-- Use Bootstrap Modal and set custome Width and Height for the Modal -->
    <div class="modal-dialog" style="width: 900px; height: 900px;">

        <!-- Define modal-content -->
        <div class="modal-content ">

            <!-- Define modal-header -->
            <div class="modal-header">
                <!-- Add Close Button -->
                <button type="button" class="close" data-dismiss="modal">
                    <span aria-hidden="true">&times;</span><span class="sr-only">Close</span>
                </button>
                <h4>
                    Data View
                    <label style="color: #0C12B0;">
                        <small>States</small>
                    </label>
                </h4>
            </div>
            <!-- End Of modal-header -->

            <!-- Define modal-Body -->
            <div class="modal-body" style="width: 100%; height: 100%;"
                 align="left">

                <div class="row">
                    <div class="col-md-12 col-xs-12 col-sm-12 col-lg-12">

                        <!-- jqGrid Component Table -->
                        <table id="SPList" style="width: 100%;font-size: 12px;"
                               class="table table-condensed table-bordered  table-hover" cellspacing="0">

                            <thead>
                            <th></th>
                            <th style="width:200px;">Stockist Point Name</th>
                            <th>Latitude</th>
                            <th>Longitude</th>
                            <th>Total Sales</th>
                            <th>Color</th>
                            <th>Size</th>
                            <th>Total Sales</th>
                            <th>Per Capita Sales(Total Scope)</th>
                            <th>Total Scope</th>
                            <th>Per Capita Sales (Covered Scope)</th>
                            <th>Type Id</th>
                            <th>Sub List</th>
                            <th>City Population</th>
                            <th>Per Capita Sales (Covered Scope)</th>
                            <th>Covered Scope</th>
                            <th>Diff Sales Color</th>

                            </thead>
                        </table>

                        <div style="float: left; height: 15px; width: 100%;" align="left"></div>

                    </div>
                </div>

                <!-- Actual Form for Data Submitting -->

            </div>

            <!-- End Of modal-body -->

            <!-- Define modal-Footer -->
            <div class="modal-footer" style="width: 100%;" align="center">

                <div style="float: left; width: 100%;" align="center">
                    <label style="color: #0C12B0;">
                        <small>Click close button or press 'Esc' button to close
                        this window</small>
                    </label>
                </div>

            </div>
            <!-- End Of modal-footer -->
        </div>

    </div>

    <!-- End Of modal-content -->

</div>
<!-- End Of modal-dialog -->

<!-- End Of showSPModal -->


<div class="summary" id="summary" style="width: 300px;">

    <div class="row">
        <div class="col-md-6" align="left">
            <div style="color: white; font-size: 14px; ">Summary</div>
            <g:hiddenField name="month" id="month"/>

        </div>

        <div class="col-md-6" align="right">
            <a href="#" onclick="prevMonth();" style="color: red; font-size: 12px;"><i
                    class="fa fa-chevron-left"></i></a>
            <small><label style="color: yellow;" id="monthStr"></label> <label style="color: limegreen;"
                                                                               id="year"></label></small>
            <a href="#" onclick="nextMonth();" style="color: red; font-size: 12px;"><i
                    class="fa fa-chevron-right"></i></a>
        </div>
    </div>


    <div class="row" align="left">

        <div class="col-md-12" align="left">
            <table id="summaryList" style="width: 100%; font-size: 12px;"
                   class="table table-condensed table-bordered"
                   cellspacing="0">

                <tr>
                    <td style="color: white;">
                        Total Population
                    </td>
                    <td align="right" style="color: yellow; font-weight: bold">
                        <label id="totPop"></label>
                    </td>
                </tr>

                <tr>
                    <td style="color: white;">
                        Total Sales
                    </td>
                    <td align="right" style="color: limegreen; font-weight: bold">
                        <label id="totSales"></label>
                    </td>
                </tr>

                <tr>
                    <td style="color: white;">
                        Per Capita Sales(Total Scope)
                    </td>
                    <td align="right" style="color: yellow; font-weight: bold">
                        <label id="avgSalesPerPop"></label>
                    </td>
                </tr>

            </table>
        </div>
    </div>

</div>


<div class="mapTitle" id="mapTitle">
    <h7><label id="lblMapTitle"></label></h7>
</div>

<div class="salesDtls" id="salesDtls" style="width: 300px;height:450px; overflow: auto;">

    <div class="row">
        <div class="col-md-12" align="left">
            <label id="salesDtlsTitle"></label>
        </div>

    </div>

    <div class="row" align="left">

        <div class="col-md-12" align="left">
            <table id="salesList" style="background-color: whitesmoke ; width: 100%; font-size: 12px;"
                   class="table table-condensed table-bordered"
                   cellspacing="0">

            </table>
        </div>
    </div>

</div>


<div class="popSwitchWindow" id="popSwitchWindow" style="display:none;width: 500px;height:300px; overflow: auto;">

    <div style="width: 100%" class="container-fluid" align="center">
        <div class="row" align="left">
            <div class="col-md-6" align="left">
                <div style=" font-size: 12px; ">Population Ranges</div>
            </div>

            <div class="col-md-6" align="right">
                <g:checkBox id="popSwitch" disabled="disabled" name="popSwitch"
                            onchange="refreshMapView()"
                            data-size="mini"/>
            </div>
        </div>



        <div class="row" align="left" style="font-size: 12px;">

            <div class="col-md-6">
                <input id="popFilter" disabled="disabled" type="checkbox"
                       onchange="refreshMapView()"> Apply
            </div>

            <div class="col-md-6">

                Radius &nbsp; <input id="spRadius" type="text" value="0" maxlength="4"
                                     style="text-align: right; color: darkblue; width: 40px;" onblur="changeRadius()">&nbsp; Km
            </div>
        </div>
        <div style="width: 100%; height: 25px;"></div>

        <div class="row" align="center" style="font-size: 12px;">

            <div class="col-md-12">
                <input id="popSlider" disabled="disabled" type="hidden" value="0,10000"/>
            </div>
        </div>

    </div>

</div>


<div class="row" style="">
    <div id="mapDiv" class="col-md-12">

        <div id="googleMap" style="width:100%;height:600px;"></div>





        %{--<input id="excepSummary" onfocus="showExcepWindow()"--}%
        %{--style="border: none; width: 300px; height: 20px; font-size: 12px; color:gray; font-style: italic "--}%
        %{--placeholder="Exceptions"> &nbsp;<i onclick="showExcepWindow()" class='fa fa-search'></i>--}%

    </div>
</div>
%{--<a href="#" class="handle" id="handle" onclick="zoomMap();"><i class='fa fa-search-plus'></i> View Fullscreen</a>--}%

</div>

%{--<div id="dataDiv" class="col-md-6">--}%

%{--<div class="row">--}%
%{--<div class="col-md-12">--}%
%{--<input type="text" id="searchField" placeholder="Search user code, name or email."--}%
%{--style="width: 100%">--}%
%{--</div>--}%
%{--</div>--}%

%{--<div class="row">--}%
%{--<div class="col-md-12">--}%
%{--<table id="stateTableList" class="table table-condensed table-bordered  table-hover" cellspacing="0">--}%
%{--<thead style="color: black; background-color: lightgrey;">--}%
%{--<th><small>No</small></th>--}%
%{--<th>Id</th>--}%
%{--<th><small>State</small></th>--}%
%{--<th>Latitude</th>--}%
%{--<th>Longitude</th>--}%
%{--<th><small>Households</small></th>--}%
%{--<th><small>Population</small></th>--}%
%{--</thead>--}%
%{--</table>--}%
%{--</div>--}%
%{--</div>--}%

%{--</div>--}%
</div>

<script>

    var screenHeight = $(window).height();
    var newHeight = Number(screenHeight);
    $('#googleMap').css("height", newHeight);
    $('#mapDiv').css("height", newHeight);

</script>

</body>
</html>
