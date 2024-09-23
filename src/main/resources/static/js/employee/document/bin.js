$(function () {
    // memberNo 받아오기 
    var memberNo = document.getElementById("mem_no").textContent;
    const csrfToken = $('input[name="_csrf"]').val();
    
    // 페이지당 리스트 10개, 페이징 요소들 넣을 영역 지정 
    const pageSize = 10; 
    const paginationDiv = document.getElementById('pagination');
    let totalPages = 0;
    let currentPage = 0;
    
    // 날짜 포맷 함수
    function formatDate(dateString) {
        const date = new Date(dateString);
        const year = date.getFullYear();
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);
        return `${year}-${month}-${day}`;
    }
	    // 모든 파일 사이즈 가져오기 
    function getAllFileSize(){
		$.ajax({
            type: 'GET',
            url: '/bin/fileSize',
            data: { memberNo: memberNo },
            dataType: 'json',
            success: function(data) {
				const totalSize = 10;
				const currentSize = $('#current_size_text');
				const currentPercent = $('#print_size');	
				const sizeBar = $('#bar_foreground');
				if(data != null){
					currentSize.text('');	
					currentSize.text('10GB 중 ' + data + 'GB 사용');	
					currentPercent.text('');
					currentPercent.text('저장용량(' + (data/totalSize)*100 + '% 사용 중)');		
					sizeBar.css('width', (data/totalSize)*100+'%');							
				} else{
					currentSize.text('');	
					currentSize.text('10GB 중 0GB 사용');	
					currentPercent.text('');
					currentPercent.text('저장용량(0% 사용 중)');	
					sizeBar.css('width', '0%');									
				}
			}
		});
	}
    // 휴지통 파일 목록을 불러오기
    function loadFiles(searchInput = '') {
        $.ajax({
            type: 'GET',
            url: '/file/bin',
            data: { memberNo: memberNo },
            dataType: 'json',
            success: function(data) {
                // 정렬 기준 가져오기
                const sortOption = $('#sort_select').val();
                const fileList = data;

                // 날짜&검색 필터링
                const startDate = new Date(startDateInput.value);
                startDate.setHours(0, 0, 0, 0);
                const endDate = new Date(endDateInput.value);
                endDate.setHours(23, 59, 59, 999); 

                const filteredFiles = fileList.filter(file => {
                    const fileDate = new Date(file.document_file_upload_date);
                    const isDateInRange = fileDate >= startDate && fileDate <= endDate;
                    const normalizedFileName = file.document_ori_file_name.normalize('NFC');
                    const normalizedSearchInput = searchInput.trim().normalize('NFC');

                    // 검색어가 파일 이름에 포함되는지 체크
                    const isSearchMatch = normalizedFileName.includes(normalizedSearchInput);
                    return isDateInRange && isSearchMatch;                
                });

                // 정렬
                if (sortOption === 'latest') {
                    filteredFiles.sort((a, b) => new Date(b.document_file_update_date) - new Date(a.document_file_update_date));
                } else if (sortOption === 'oldest') {
                    filteredFiles.sort((a, b) => new Date(a.document_file_update_date) - new Date(b.document_file_update_date));
                }

                // 몇 페이지인지 계산 
                totalPages = Math.ceil(filteredFiles.length / pageSize);
                const fileTableBody = document.getElementById('file_table_body');
                fileTableBody.innerHTML = '';
	            // 체크박스 초기화 
				$('#select_all').off('change');
				$('#select_all').prop('checked', false);
                // 파일 목록이 존재할 때
                if (filteredFiles.length > 0) {
                    $('.document_file_list').show();
                    $('.box_size').show();	                
                    // 체크박스 활성화 
	                $('#select_all').prop('disabled', false).prop('checked', false);                 
                    // 한 페이지에 10개씩 추가 
                    const start = currentPage * pageSize;
                    const end = Math.min(start + pageSize, filteredFiles.length);
                    const paginatedFiles = filteredFiles.slice(start, end);

                    paginatedFiles.forEach(file => {
                        const row = document.createElement('tr');
                        row.innerHTML = `
                            <td><input type="checkbox" class="file_checkbox"></td>
                            <td>${file.document_ori_file_name}</td>
                            <td> 
                            	${file.document_box_type === 0 ? '개인 문서함' : 
              					file.document_box_type === 1 ? '부서 문서함' : 
              					file.document_box_type === 2 ? '사내 문서함' : ''}			   
                            </td>
                            <td>${formatDate(file.document_file_update_date)}</td>
                            <td>${file.document_file_size}</td>
                            <td><input type="button" value="복구" class="file_update_button"></td>
                            <td><input type="button" class="delete_button" value="영구 삭제" id="${file.document_file_no}"></td>
                        `;
                        fileTableBody.appendChild(row);
                    });

                    // 리스트가 10개가 안 된다면 빈 행 추가로 10개 만들기 
                    const emptyRows = pageSize - paginatedFiles.length;
                    for (let i = 0; i < emptyRows; i++) {
                        const emptyRow = document.createElement('tr');
                        emptyRow.innerHTML = `
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                            <td></td>
                        `;
                        fileTableBody.appendChild(emptyRow);
                    }

                    // 페이징 업데이트 
                    updatePagination();
                } else {
                    $('.document_file_list').show();
                    fileTableBody.innerHTML = '<tr><td colspan="7">파일 목록이 존재하지 않습니다.</td></tr>';
                    // 페이징 버튼 숨기기
                    paginationDiv.innerHTML = '';
 	                // 체크박스 비활성화 
	                $('#select_all').prop('disabled', true);                   
                }
                // 파일 영구 삭제 
				$('.delete_button').on('click', function() {
	                const fileNo = this.id;
	                deleteFile(fileNo);
	            });
                // th 체크박스 클릭하면 전부 선택
                $('#select_all').on('change', function() {
                    const isChecked = this.checked; 
                    $('.file_checkbox').prop('checked', isChecked); 
                });
 	            // 파일 선택 삭제
	            $('#select_delete').on('click', function() {
	                const selectedFileNos = []; 
	                
	                // 체크된 파일들의 fileNo 가져오기 
	                $('.file_checkbox:checked').each(function() {
	                    const fileNo = $(this).closest('tr').find('.delete_button').attr('id');
	                    selectedFileNos.push(fileNo); 
	                });
	                if (selectedFileNos.length > 0) {
	                    deleteSelectedFile(selectedFileNos);
	                } else {
	                    Swal.fire({
	                        icon: 'warning',
	                        text: '삭제할 파일을 선택해 주세요.',
	                        confirmButtonText: '확인'
	                    });
	                }
	            });      
	            // 파일 복구 
				$('.file_update_button').on('click', function() {
	                const fileNo = $(this).closest('tr').find('.delete_button').attr('id');
	                updateFile(fileNo);
	            });     
            }
        });
    }

    // 페이징 버튼 업데이트 
    function updatePagination() {
        paginationDiv.innerHTML = '';

        // 총 페이지 수가 1일 때
        if (totalPages <= 1) {
            const pageButton = document.createElement('span');
            pageButton.className = 'pagination_button active';
            pageButton.textContent = '1';
            paginationDiv.appendChild(pageButton);
            return;
        }

        // 처음 페이지 버튼 (<<)
        if (currentPage > 0) {
            const firstButton = document.createElement('span');
            firstButton.className = 'go_first_page_button';
            firstButton.textContent = '<<';
            firstButton.onclick = () => {
                currentPage = 0;
                loadFiles();
            };
            paginationDiv.appendChild(firstButton);
        }

        // 이전 페이지 버튼 (<)
        if (currentPage > 0) {
            const prevButton = document.createElement('span');
            prevButton.className = 'pagination_button';
            prevButton.textContent = '<';
            prevButton.onclick = () => {
                currentPage--;
                loadFiles();
            };
            paginationDiv.appendChild(prevButton);
        }

        // 페이지 번호 버튼 (최대 3개 표시)
        let startPage = Math.max(0, currentPage - 1);
        let endPage = Math.min(totalPages - 1, currentPage + 1);

        for (let page = startPage; page <= endPage; page++) {
            const pageButton = document.createElement('span');
            pageButton.className = `pagination_button ${page === currentPage ? 'active' : ''}`;
            pageButton.textContent = page + 1;
            pageButton.onclick = () => {
                currentPage = page;
                loadFiles();
            };
            paginationDiv.appendChild(pageButton);
        }

        // 다음 페이지 버튼 (>)
        if (currentPage < totalPages - 1) {
            const nextButton = document.createElement('span');
            nextButton.className = 'pagination_button';
            nextButton.textContent = '>';
            nextButton.onclick = () => {
                currentPage++;
                loadFiles();
            };
            paginationDiv.appendChild(nextButton);
        }

        // 마지막 페이지 버튼 (>>)
        if (currentPage < totalPages - 1) {
            const lastButton = document.createElement('span');
            lastButton.className = 'go_last_page_button';
            lastButton.textContent = '>>';
            lastButton.onclick = () => {
                currentPage = totalPages - 1;
                loadFiles();
            };
            paginationDiv.appendChild(lastButton);
        }
    }

	// 파일 영구 삭제
	function deleteFile(fileNo){
		Swal.fire({
			icon: 'warning',
		    text: '정말 영구 삭제하시겠습니까?',
		    showCancelButton: true,
		    confirmButtonText: '확인',
		    cancelButtonText: '취소'
		}).then((result) => {
			if (result.isConfirmed) {
				$.ajax({
					type: 'POST',
					url: '/document/file/permanent/delete',
					data: {
						fileNo: fileNo
					},
					headers: {
	                    'X-Requested-With': 'XMLHttpRequest',
	                    'X-CSRF-TOKEN': csrfToken
	                },
					success: function(response){
						if (response.res_code === '200') {
			                    Swal.fire({
			                        icon: 'success',
			                        text: response.res_msg,
			                        confirmButtonText: '확인'
			                    });
			            	loadFiles();
			            	getAllFileSize();
	                    } else {
	                        Swal.fire({
		                        icon: 'error',
		                        text: response.res_msg,
		                        confirmButtonText: '확인'
		                    });
	                    }
	                     $('#file_name_input').val(''); 
					}
				});
			}
		})
	}
	// 파일 선택 영구 삭제
	function deleteSelectedFile(fileNos) {
	    Swal.fire({
	        icon: 'warning',
	        text: '정말 삭제하시겠습니까?',
	        showCancelButton: true,
	        confirmButtonText: '확인',
	        cancelButtonText: '취소'
	    }).then((result) => {
	        if (result.isConfirmed) {
	            $.ajax({
	                type: 'POST',
	                url: '/document/fileList/permanent/delete',  
	                contentType: 'application/json',
	                data: JSON.stringify({ 
						fileNos: fileNos 
					}), 
	                headers: {
	                    'X-Requested-With': 'XMLHttpRequest',
	                    'X-CSRF-TOKEN': csrfToken
	                },
	                success: function(response) {
	                    if (response.res_code === '200') {
	                        Swal.fire({
	                            icon: 'success',
	                            text: response.res_msg,
	                            confirmButtonText: '확인'
	                        });
	                        loadFiles(); 
	                        getAllFileSize();
	                    } else {
	                        Swal.fire({
	                            icon: 'error',
	                            text: response.res_msg,
	                            confirmButtonText: '확인'
	                        });
	                    }
	                     $('#file_name_input').val(''); 
	                }
	            });
	        }
	    });
	}	
	// 파일 복구 
	function updateFile(fileNo){
		$.ajax({
			type: 'POST',
			url: '/document/file/update',
			data: JSON.stringify({ fileNo: fileNo }),
			contentType: 'application/json',
			headers: {
                'X-Requested-With': 'XMLHttpRequest',
                'X-CSRF-TOKEN': csrfToken
            },
			success: function(response){
				if (response.res_code === '200') {
	                    Swal.fire({
	                        icon: 'success',
	                        text: response.res_msg,
	                        confirmButtonText: '확인'
	                    });
	            	loadFiles();
	            	getAllFileSize();
                } else {
                    Swal.fire({
                        icon: 'error',
                        text: response.res_msg,
                        confirmButtonText: '확인'
                    });
                }
                 $('#file_name_input').val(''); 
			},
			error: function(xhr){
				console.error(xhr.responseText);
			}
		});
	}	
	// 날짜 검색 
	var today = new Date();
	const todayStr = formatDate(today);	
	// 1년 전 날짜 계산
	const oneYearAgo = new Date();
	oneYearAgo.setFullYear(today.getFullYear() - 1);
	const oneYearAgoStr = formatDate(oneYearAgo);
	const startDateInput = document.getElementById('start_date');
    const endDateInput = document.getElementById('end_date');
    // 시작 날짜를 끝나는 날보다 나중 날짜로 설정 못하게 하는 함수 
    function startDateLimit() {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);
        // endDate가 startDate보다 이전일 때 startDate를 endDate와 같게 설정
	    if (endDate < startDate) {
	        startDateInput.value = formatDate(endDate);
	    }	    
	    // startDate의 최대값을 endDate로 설정
	    startDateInput.max = formatDate(endDate);
    }
    // startDate의 기본값을 오늘로부터 1년 전으로 설정
	startDateInput.value = oneYearAgoStr;
    // endDate의 기본 값을 오늘로 설정 
    endDateInput.value = todayStr;
	
	// startDate와 endDate를 오늘 이후의 날짜를 설정할 수 없게 설정 
    startDateInput.max = todayStr;
    endDateInput.max = todayStr;
    
    // 파일 검색 
   $('#search_button').on('click', function(){
		const searchInput = $('#file_name_input').val();
		loadFiles(searchInput);
   });

    // 페이지가 로드될 때 파일 목록을 불러옴
    $(document).ready(function() {
        loadFiles(); // 페이지 로드 시 파일 목록 로드
        getAllFileSize();

        // 정렬 선택이 변경될 때 파일 목록을 다시 불러옴
        $('#sort_select').on('change', function() {
            loadFiles();
        });

        // 날짜가 변경될 때 파일 목록을 새로 로드
        startDateInput.addEventListener('change', function() {
            startDateLimit();
            loadFiles();
        });

        endDateInput.addEventListener('change', function() {
            startDateLimit();
            loadFiles();
        });
    });

    // 시작 날짜를 끝나는 날보다 나중 날짜로 설정 못하게 하는 함수 
    function startDateLimit() {
        const startDate = new Date(startDateInput.value);
        const endDate = new Date(endDateInput.value);
        // endDate가 startDate보다 이전일 때 startDate를 endDate와 같게 설정
        if (endDate < startDate) {
            startDateInput.value = formatDate(endDate);
        }        
        // startDate의 최대값을 endDate로 설정
        startDateInput.max = formatDate(endDate);
    }
});