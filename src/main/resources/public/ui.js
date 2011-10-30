function runAnts() {
      function renderActivity(activity, templateId) {
        var template = $(templateId).html();
        return Mustache.to_html(template, activity);
      }
      
      google.load("visualization", "1", {packages:["corechart"]});

      console.log("initializing...");
      var resultsData;
      
      // preview settings
      var scale = 64;
      
      var antsData;
      var iterationsData = { "iteration" : [] };
      var currentGameId;
      var isTailing = false;
      var gridView = 'contested';
      
      var pageCoords = {"top": 0, "left": 0, "bottom": scale-1, "right": scale-1};
      
      function trim(resultsData) {
        if(resultsData && resultsData.rows && resultsData.rows.length > 0) {
          var rowsIn = resultsData.rows; 
          var rows = [];
          for(var i = Math.min(rowsIn.length, pageCoords.top); i < Math.min(rowsIn.length, pageCoords.bottom); i++) {
            var cells = [];
            var cellsIn = resultsData.rows[i].cells;
            for(var j = Math.min(cellsIn.length, pageCoords.left); j < Math.min(cellsIn.length, pageCoords.right); j++) {
              cells.push(cellsIn[j]);
            }
            rows.push({"cells": cells});
          }
          var trimmed = { "summary": resultsData.summary, "rows": rows };
          return trimmed;
        } else { 
          return resultsData;
        }
      }
      
      function summarizeRegion(resultsData, top, left) {
        var summary = {};
        var heat = 0;
        var p1Heat = 0;
        var p2Heat = 0;
        var contestedHeat = 0;
        if(resultsData && resultsData.rows && resultsData.rows.length > 0) {
          var rowsIn = resultsData.rows; 
          for(var i = Math.min(rowsIn.length, top); i < Math.min(rowsIn.length, top+scale-1); i++) {
            var cellsIn = resultsData.rows[i].cells;
            for(var j = Math.min(cellsIn.length, left); j < Math.min(cellsIn.length, left+scale-1); j++) {
              var cell = cellsIn[j];
              if(cell.nest) {
                summary.nest = cell.nest;
              }
              if(cell.p1Heat) {
                heat = heat + parseInt(cell.p1Heat);
              }
              if(cell.p2ContestedHeat) {
                heat = heat + parseInt(cell.p2Heat);
              }
            }
          }
        }
        
        if(heat > 0) {
          heat = heat/700;
          var limits = [5120, 2560, 1280, 640, 320, 160, 80, 40, 20, 10, 5];
          for(var i=0; i< limits.length; i++) {
            var limit = limits[i];
            if(heat > limit) {
              heat = limit;
              break;
            }
          }
        }
              
        summary.heat = heat;
        return summary;
      }
      
      function buildPreview(resultsData) {
        if(resultsData && resultsData.rows && resultsData.rows.length > 0) {
          var rows = resultsData.rows.length;
          var columns = resultsData.rows[0].cells.length;
          
          var previewRows = rows/scale;
          var previewColumns = columns/scale;
          
          var rows = [];
          for(var i = 0; i < previewRows; i++) {
            var cells = [];
            for(var j = 0; j < previewColumns; j++) {
              var summary = summarizeRegion(resultsData,i*scale, j*scale);
              //var summary = {};
              summary.top = i*scale;
              summary.left = j*scale;
              if(i*scale == pageCoords.top && j*scale == pageCoords.left) {
                summary.selected = "true";
              }
              cells.push(summary);
            }
            rows.push({"cells" : cells });
          }
          return { "rows" : rows };
        } else {
          return {};
        }
      }
      
      function selectPage() {
      }
      
      function enableTailing() {
        isTailing = true;
        $('#tail-button').addClass('tailing-selected');
      }
      
      function disableTailing() {
        isTailing = false;
        $('#tail-button').removeClass('tailing-selected');
      }
      
      $('#view-p1-button').click(function(){
        updateGridView('p1');
      });
      
      $('#view-p2-button').click(function(){
        updateGridView('p2');
      });
      
      $('#view-food-button').click(function(){
        updateGridView('food');
      });
      
      $('#view-contested-button').click(function(){
        updateGridView('contested');
      });
      
      $('#view-p1HomePheromone-button').click(function(){
        updateGridView('p1HomePheromone');
      });
      
      $('#view-p1FoodPheromone-button').click(function(){
        updateGridView('p1FoodPheromone');
      });
      
      $('#view-timeline-button').click(function(){
        $('.view-selected').removeClass('view-selected');
        $(this).addClass('view-selected');
        drawTimeline();
        $('#grid').html('');
      });
      
      function updateGridView(view) {
        clearTimeline();
        gridView = view;
        $('.view-selected').removeClass('view-selected');
        $('#view-' + view + '-button').addClass('view-selected');
        $("#grid").html(renderActivity(trim(resultsData), "#template-grid-" + gridView));
        $("#preview").html(renderActivity(buildPreview(resultsData), "#template-preview"));
        
        $('.preview-cell').click(function(){
          var top = $(this).data('top');
          var left = $(this).data('left');

          pageCoords = {"top": top, "left": left, "bottom": top+scale-1, "right": left+scale-1};
          $("#preview").html(renderActivity(buildPreview(data), "#template-preview"));
          updateGridView(gridView);
        });
        
      }
      
      renderIterations();
      
      
      $('#pause-button').click(function(){
        pause();
      });

      $('#play-button').click(function(){
        play();
      });
      
      $('#tail-button').click(function(){
        if(isTailing) disableTailing();
        else enableTailing();
      });
      
      $('#purge-button').click(function() {
        if(confirm("Are you sure you want to permanently remove all history?")) {
          $.ajax({
            type: 'GET',
            url: '/ants/nest/purge',
            success: function (data) { doLoadHistory(data); }
          });
        }
      });
      
      $('#ants-panel-button').click(function(){
        console.log("ants panel toggle");
        var panel = $('#ants-panel');
        console.log("hidden? " + panel.is(":hidden"));
        if(panel.is(":hidden")) {
          renderAnts();
        }
        
        panel.toggle();
        return false;
      });
      
      $('#iterations-panel-button').click(function(){
        console.log("iterations panel toggle");
        var panel = $('#iterations-panel');
        if(panel.is(":hidden")) {
          renderIterations();
        }
        
        panel.toggle();
        return false;
      });
      
      function renderAnts() {
        $("#ants").html(renderActivity({}, '#template-ants'));
      }
      
      function renderIterations() {
        if(iterationsData && iterationsData.iteration) {
          $("#iterations").html(renderActivity(iterationsData, '#template-iterations'));
          $('#'+currentGameId).addClass('selected-iteration');
          
          $('#iterator-table tr').click(function(){
            var id = $(this).data('id');
            loadIteration(id);
            disableTailing();
          });
        }
      }
      
      function loadHistory() {
        $.ajax({
          type: 'GET',
          url: '/ants/nest/ants_full_history',
          success: function (data) { doLoadHistory(data); }
        });
      }
      
      function doLoadHistory(data) {
        if(data.iteration) {
          iterationsData = data;
          iterationsData.iteration.reverse();
          if(iterationsData.iteration.length > 500) {
            iterationsData.iteration.splice(0, iterationsData.iteration.length-500);
          }
          renderIterations();
          if(isTailing) {
            var last = data.iteration[0];
            loadIteration(last.id);
          }
        }
      }
      
      function loadIteration(id) {
        $.ajax({
          type: 'GET',
          url: '/ants/nest/' + id,
          success: function(data) {
            if(data.rows) {
              resultsData = data;
              pageCoords = {"top": 0, "left": 0, "bottom": scale-1, "right": scale-1};
              $("#grid").html(renderActivity(trim(data), "#template-grid-" + gridView));
              $("#data").html(renderActivity(data, "#template-data"));
              $("#preview").html(renderActivity(buildPreview(data), "#template-preview"));
              //drawTimeline(resultsData.summary.p1Stats);
              clearTimeline();
              
              $('.preview-cell').click(function(){
                var top = $(this).data('top');
                var left = $(this).data('left');

                pageCoords = {"top": top, "left": left, "bottom": top+scale-1, "right": left+scale-1};
                $("#preview").html(renderActivity(buildPreview(data), "#template-preview"));
                updateGridView(gridView);
              });
              
              if(currentGameId) {
                $('#'+currentGameId).removeClass('selected-iteration');
              }
              $('#'+id).addClass('selected-iteration');
              currentGameId = id;
            }
          }
        });
      }
      
      function clearTimeline() {
        $("#timeline").html("");
        $("#moves-writes").html("");
      }
      
      function drawTimeline() {
        var playerStats = resultsData.summary.p1Stats;
        var movesWrites = new google.visualization.DataTable();
        movesWrites.addColumn('string', 'Turn');
        movesWrites.addColumn('number', 'Moves');
        movesWrites.addColumn('number', 'Writes');
        movesWrites.addRows(playerStats.histogramBucketCount);
        
        var data = new google.visualization.DataTable();
        data.addColumn('string', 'Turn');
        //data.addColumn('number', 'Moves');
        data.addColumn('number', 'Gets');
        data.addColumn('number', 'Drops');
        data.addColumn('number', 'Speaks');
        //data.addColumn('number', 'Writes');
        data.addRows(playerStats.histogramBucketCount);
        
        for(var bucket = 0; bucket < playerStats.histogramBucketCount; bucket++) {
          var moveCount = playerStats.moveHistogram[bucket];
          var attackCount = playerStats.attackHistogram[bucket];
          var getCount = playerStats.getHistogram[bucket];
          var dropCount = playerStats.dropHistogram[bucket];
          var speakCount = playerStats.speakHistogram[bucket];
          var writeCount = playerStats.writeHistogram[bucket];
          
          data.setValue(bucket, 0, ''+ ((bucket+2)*500));
          //data.setValue(bucket, 1, moveCount);
          data.setValue(bucket, 1, getCount);
          data.setValue(bucket, 2, dropCount);
          data.setValue(bucket, 3, speakCount);
          //data.setValue(bucket, 5, writeCount);
          
          movesWrites.setValue(bucket, 0, ''+ ((bucket+2)*500));
          movesWrites.setValue(bucket, 1, moveCount);
          movesWrites.setValue(bucket, 2, writeCount);
        }

        var chart = new google.visualization.LineChart(document.getElementById('timeline'));
        chart.draw(data, {width: 800, height: 240, title: 'P1 Timeline'});
        
        var chart2 = new google.visualization.LineChart(document.getElementById('moves-writes'));
        chart2.draw(movesWrites, {width: 800, height: 240, title: 'P1 Moves/Writes'});
      }

      var i;

      function play() {
        i = setInterval(function(){
          loadHistory();
        }, 1000);
      }

      function pause() {
	      clearTimeout(i);
      }

      //enableTailing();
      loadHistory();
      play();
}

// disabled since google charts now loads this method after starting up
//$().ready(runAnts);
