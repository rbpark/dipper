var leftExpanded = false;

function toggleLeftMenu() {
	var width = $('#leftmenu').width();
	if (leftExpanded) {
		$('#leftmenu').animate({'left':'-=' + width + 'px'}, 'fast');
		$('#leftArrow').removeClass('expanded');
		$('#leftArrow').addClass('unexpanded');
	}
	else {
		$('#leftmenu').animate({'left':'+=' + width + 'px'}, 'fast');
		$('#leftArrow').removeClass('unexpanded');
		$('#leftArrow').addClass('expanded');
	}
	leftExpanded = !leftExpanded;
}

var rightExpanded = false;
function toggleRightMenu() {
	var width = $('#rightmenu').width();
	if (rightExpanded) {
		$('#rightmenu').animate({'right':'-=' + width + 'px'}, 'fast');
		$('#rightArrow').removeClass('expanded');
		$('#rightArrow').addClass('unexpanded');
	}
	else {
		$('#rightmenu').animate({'right':'+=' + width + 'px'}, 'fast');
		$('#rightArrow').removeClass('unexpanded');
		$('#rightArrow').addClass('expanded');
	}
	rightExpanded = !rightExpanded;
}

function initMenus() {
	var leftWidth = $('#leftmenu').width();
	var rightLeft = $('#rightmenu').width();
	
	$('#leftmenu').css({'left': '-' + leftWidth + "px"});
	$('#rightmenu').css({'right': '-' + rightLeft + "px"});
	
	$('#leftArrow').click(function() {
		toggleLeftMenu();
	});	
	
	$('#rightArrow').click(function() {
		toggleRightMenu();
	});	
}

$(function() {
	initMenus();
});