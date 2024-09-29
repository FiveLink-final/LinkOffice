document.addEventListener('DOMContentLoaded', function() {
    var calendarEl = document.getElementById('meeting_calendar');
    var calendar;

    const today = new Date();
	const formattedToday = `${today.getFullYear()}-${('0' + (today.getMonth() + 1)).slice(-2)}-${('0' + today.getDate()).slice(-2)}`;
 
    $.ajax({
        url: '/home/reservations',
        method: 'GET',
        dataType: 'json',
        success: function(reservations) {
            initializeCalendar(reservations);
            displayReservations(formattedToday, reservations);   
        } 
    });

    function initializeCalendar(reservations) {
        calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth',
            locale: 'ko',
            headerToolbar: {
                left: 'prev,next today',
                center: 'title',
                right: ''
            },
            buttonText: {
	            today: '오늘'
	        },
            contentHeight: 'auto',
	        handleWindowResize: true,
	        fixedWeekCount: false, 
            events: reservations.map(function(reservation) {
                return {
                    id: reservation.meeting_reservation_no,
                    title: reservation.meeting_name,
                    start: reservation.meeting_reservation_date,
                    extendedProps: {
                        startTime : reservation.meeting_reservation_start_time,
                        endTime : reservation.meeting_reservation_end_time,
                        purpose: reservation.meeting_reservation_purpose,
                        participantCount: reservation.participant_count,
                        member_name : reservation.member_name,
                        member_no : reservation.member_no,
                        departmentName : reservation.department_name,
                        positionName : reservation.position_name
                    }
                };
            }),
            dayCellContent: function(info) {
                var number = document.createElement("a");
                number.classList.add("fc-daygrid-day-number");
                number.innerHTML = info.dayNumberText.replace("일", '').replace("日", "");

                var wrapper = document.createElement("div");
                wrapper.classList.add("fc-daygrid-day-top");
                wrapper.appendChild(number);

                if (info.view.type === "dayGridMonth") {
                    return { domNodes: [wrapper] };
                }
                return { domNodes: [] };
            },
            dateClick: function(info) {
                displayReservations(info.dateStr, reservations);
            },
            eventClick: function(info) {
	            const eventDate = new Date(info.event.start);
	            eventDate.setDate(eventDate.getDate() + 1); 
	            const formattedDate = eventDate.toISOString().split('T')[0]; 
	            displayReservations(formattedDate, reservations); 
	        }
        });
        calendar.render();
    }

    function displayReservations(date, reservations) {
        var reservationList = document.getElementById('meeting_selected_date_list');
        reservationList.innerHTML = '';

        const home_schedule_date = document.getElementById('meeting_home_date');
 
        const formattedDate = date.split('-').join('.'); 
        home_schedule_date.innerHTML = formattedDate; 

        var dayReservations = reservations.filter(function(reservation) {
            return reservation.meeting_reservation_date.startsWith(date);
        });

        if (dayReservations.length === 0) {
            reservationList.innerHTML = '<div>예약이 없습니다.</div>';
        } else {
            dayReservations.forEach(function(reservation) {
                var div = document.createElement('div');
                div.textContent = '[' + reservation.meeting_name + '] ' +  reservation.meeting_reservation_start_time + ' ~ ' + reservation.meeting_reservation_end_time 
                + ' ' + reservation.member_name + ' ' + reservation.position_name + ' (' + reservation.participant_count + '명)';
                reservationList.appendChild(div);
            });
        }
    }
});