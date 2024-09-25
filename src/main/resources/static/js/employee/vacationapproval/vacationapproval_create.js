// editor

import {
    ClassicEditor,
    AccessibilityHelp,
    Autosave,
    Bold,
    Essentials,
    FontBackgroundColor,
    FontColor,
    FontFamily,
    FontSize,
    Heading,
    ImageBlock,
    ImageInline,
    ImageToolbar,
    Italic,
    Link,
    List,
    Paragraph,
    SelectAll,
    Strikethrough,
    Table,
    TableCaption,
    TableCellProperties,
    TableColumnResize,
    TableProperties,
    TableToolbar,
    Underline,
    Undo
} from 'ckeditor5';

const editorConfig = {
    toolbar: {
        items: [
            'undo',
            'redo',
            '|',
            'heading',
            '|',
            'fontSize',
            'fontFamily',
            'fontColor',
            'fontBackgroundColor',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            '|',
            'link',
            'insertTable',
            '|',
            'bulletedList',
            'numberedList'
        ],
        shouldNotGroupWhenFull: false
    },
    plugins: [
        AccessibilityHelp,
        Autosave,
        Bold,
        Essentials,
        FontBackgroundColor,
        FontColor,
        FontFamily,
        FontSize,
        Heading,
        ImageBlock,
        ImageInline,
        ImageToolbar,
        Italic,
        Link,
        List,
        Paragraph,
        SelectAll,
        Strikethrough,
        Table,
        TableCaption,
        TableCellProperties,
        TableColumnResize,
        TableProperties,
        TableToolbar,
        Underline,
        Undo
    ],
    fontFamily: {
        options: [
            '돋움, Dotum, sans-serif',
            '굴림, Gulim, sans-serif',
            '바탕, Batang, serif',
            '나눔고딕, NanumGothic, sans-serif'
        ],
        supportAllValues: true
    },
    fontSize: {
        options: [10, 12, 14, '16', 18, 20, 22],
        supportAllValues: true
    },
    heading: {
        options: [
            { model: 'paragraph', title: '굵기', class: 'ck-heading_paragraph' },
            { model: 'heading1', view: 'h1', title: '굵기 1', class: 'ck-heading_heading1' },
            { model: 'heading2', view: 'h2', title: '굵기 2', class: 'ck-heading_heading2' },
            { model: 'heading3', view: 'h3', title: '굵기 3', class: 'ck-heading_heading3' },
            { model: 'heading4', view: 'h4', title: '굵기 4', class: 'ck-heading_heading4' },
            { model: 'heading5', view: 'h5', title: '굵기 5', class: 'ck-heading_heading5' },
            { model: 'heading6', view: 'h6', title: '굵기 6', class: 'ck-heading_heading6' }
        ]
    },
    image: {
        toolbar: ['imageTextAlternative']
    },
    table: {
        contentToolbar: ['tableColumn', 'tableRow', 'mergeTableCells', 'tableProperties', 'tableCellProperties']
    }
};

// 시작 기간 선택 전 disable
document.addEventListener("DOMContentLoaded", function() {
	
const startDateInput = document.getElementById("vacationapproval_start_date");
const endDateInput = document.getElementById("vacationapproval_end_date");
const dateCountInput = document.getElementById("vacationapproval_date_count");

endDateInput.disabled = true;

let holidaysData = {};

function fetchHolidays(startDate) {
    const year = new Date(startDate).getFullYear();

    // 공휴일 API
    const xhr = new XMLHttpRequest();
    const url = 'http://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo';
    const serviceKey = 'h8Gz1P5QVyOyAWHg7Cfs2N+UQCYcsX80WVLB8Za9h0fgYD4CRN5+9L/eVnb2nG85zzjN+0i/gkQ5VeUSTqlTfg=='; 
    let queryParams = '?' + encodeURIComponent('serviceKey') + '=' + encodeURIComponent(serviceKey);
    queryParams += '&' + encodeURIComponent('solYear') + '=' + encodeURIComponent(year);
    queryParams += '&' + encodeURIComponent('numOfRows') + '=' + encodeURIComponent('100');

    xhr.open('GET', url + queryParams);
    
    xhr.onreadystatechange = function () {
        if (this.readyState === 4) {
            if (this.status === 200) {
                const parser = new DOMParser();
                const xmlDoc = parser.parseFromString(this.responseText, "text/xml");
                const items = xmlDoc.getElementsByTagName("item");

                for (let i = 0; i < items.length; i++) {
                    const locdate = items[i].getElementsByTagName("locdate")[0].textContent;
                    const dateName = items[i].getElementsByTagName("dateName")[0].textContent;

                    holidaysData[locdate] = dateName;
                }
                
            } else {
                console.error('오류 :', this.status);
            }
        }
    };

    xhr.send();
}

startDateInput.addEventListener("change", function() {
    if (startDateInput.value) {
        endDateInput.disabled = false; 
        fetchHolidays(startDateInput.value);
        calculateDateDifference();
    } else {
        endDateInput.disabled = true; 
        endDateInput.value = '';
        dateCountInput.value = ''; 
    }
});

endDateInput.addEventListener("change", function() {
    const startDate = new Date(startDateInput.value);
    const endDate = new Date(endDateInput.value);

    if (startDate && endDate && endDate < startDate) {
        Swal.fire({
            icon: 'warning',
            text: "종료일은 시작일보다 이전일 수 없습니다.",
            confirmButtonColor: '#B1C2DD',
            confirmButtonText: "확인"
        });
        endDateInput.value = '';
        dateCountInput.value = ''; 
    } else {
        calculateDateDifference(); 
    }
});

// 공휴일, 주말 제외
function calculateDateDifference() {
    const startDate = new Date(startDateInput.value);
    const endDate = new Date(endDateInput.value);

    if (startDate && endDate && endDate >= startDate) {
        let daysDifference = 0;
        
        for (let d = new Date(startDate); d <= endDate; d.setDate(d.getDate() + 1)) {
            const dayOfWeek = d.getDay();
            const locdate = d.toISOString().slice(0, 10).replace(/-/g, ''); 
            if (dayOfWeek !== 0 && dayOfWeek !== 6 && !holidaysData[locdate]) {
                daysDifference++;
            }
        }

        dateCountInput.value = daysDifference;
    } else {
        dateCountInput.value = '';
    }
}

    
       document.getElementById('vacationapproval_file').addEventListener('change', function (event) {
        const vacationFile = event.target.files[0]; 
        
        if (vacationFile) {
            const fileType = vacationFile.type;
            const validTypes = [
                'image/png', 
                'image/jpeg', 
                'application/pdf', 
                'application/vnd.ms-excel', 
                'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet'
            ];

            if (!validTypes.includes(fileType)) {
                Swal.fire({
                    icon: 'error',
                    text: 'PNG, JPG, PDF, EXCEL만 등록 가능합니다.',
                    confirmButtonColor: '#B1C2DD',
                    confirmButtonText: '확인'
                });
                event.target.value = ''; 
            }
        }
    });
    	
    
    
});



ClassicEditor.create(document.querySelector('#editor'), editorConfig)
    .then(editor => {
		
        editor.ui.view.editable.element.style.height = '500px';
			// 에디터에 휴가 양식 
            editor.setData(`
            <h1>휴가신청서</h1><figure class="table" style="width:78.93%;"><table class="ck-table-resized"><colgroup><col style="width:18.8%;"><col style="width:81.2%;"></colgroup><tbody><tr><th>신청일</th><td>&nbsp;</td></tr><tr><th>신청자</th><td>&nbsp;</td></tr><tr><th>부서</th><td>&nbsp;</td></tr><tr><th>휴가 시작일</th><td>&nbsp;</td></tr><tr><th>휴가 종료일</th><td>&nbsp;</td></tr><tr><th>휴가 종류</th><td>&nbsp;</td></tr><tr><th>휴가 사유</th><td>&nbsp;</td></tr></tbody></table></figure><p>&nbsp;</p><figure class="table" style="width:78.93%;"><table class="ck-table-resized"><colgroup><col style="width:100%;"></colgroup><thead><tr><th>특이 사항</th></tr></thead><tbody><tr><td>&nbsp;</td></tr></tbody></table></figure>`);
			
			// 등록  폼
			const form = document.getElementById('vacAppCreateFrm');
			form.addEventListener('submit', (e) => {
	  		  e.preventDefault();
	    
		    const editorData = editor.getData();
		    const vacationapprovalTitle = document.querySelector('#vacationapproval_title').value;
		    const vacationtype = document.querySelector('select[name="vacationtype"]').value;
		    const csrfToken = document.querySelector('#csrf_token').value;
		    const memberNo = document.querySelector('#member_no').value;
		    const startDate = document.querySelector('#vacationapproval_start_date').value;
		    const endDate = document.querySelector('#vacationapproval_end_date').value;
		    const dateCount = document.querySelector('#vacationapproval_date_count').value;
		    const vacationFile = document.querySelector('#vacationapproval_file').files[0];
			const approvers = Array.from(document.querySelectorAll('input[id="approverNumbers"]')).map(input => input.value);
			const references = Array.from(document.querySelectorAll('input[id="referenceNumbers"]')).map(input => input.value);
			const reviewers = Array.from(document.querySelectorAll('input[id="reviewerNumbers"]')).map(input => input.value);

		 	let vali_check = false;
            let vali_text = "";
			
            if (vacationapprovalTitle.trim() === "") {  
                vali_text += '결재 제목을 입력해주세요.';
                document.querySelector('#vacationapproval_title').focus();
            } else if(startDate.trim() === ""){
				 vali_text += '휴가 기간을 입력해주세요.';
                document.querySelector('#vacationapproval_start_date').focus();
			} else if(endDate.trim() === ""){
				 vali_text += '휴가 기간을 입력해주세요.';
                document.querySelector('#vacationapproval_end_date').focus();
			} else {
                vali_check = true;
            }
			
			if (vali_check == false) {
                Swal.fire({
                    icon: 'error',
                    text: vali_text,
                    confirmButtonColor: '#B1C2DD',
                    confirmButtonText: "확인"
                });
            } else {
				const payload = new FormData();
			    payload.append('vacationapprovalContent', editorData);
			    payload.append('vacationapprovalTitle', vacationapprovalTitle);
			    payload.append('vacationtype', vacationtype);
			    payload.append('memberNo', memberNo);
			    payload.append('startDate', startDate);
			    payload.append('endDate', endDate);
			    payload.append('dateCount', dateCount);
			    
			    if (vacationFile) {
           			payload.append('vacationFile', vacationFile);
        		}
			    payload.append('approvers', approvers);
			    payload.append('references', references);
			    payload.append('reviewers', reviewers);
			
			    fetch('/employee/vacationapproval/create', {
			        method: 'post',
			        body: payload,
			        headers: {
			            'X-CSRF-TOKEN': csrfToken
			        }
			    })
			    .then(response => response.json())
			    .then(data => {
			        if (data.res_code == '200') {
			            Swal.fire({
			                icon: 'success',
			                text: data.res_msg,
			                confirmButtonColor: '#B1C2DD',
			                confirmButtonText: "확인"
			            }).then((result) => {
			                location.href = "/employee/vacationapproval/list";
			            });
			        } else {
			            Swal.fire({
			                icon: 'error',
			                text: data.res_msg,
			                confirmButtonColor: '#B1C2DD',
			                confirmButtonText: "확인"
			            });
			        }
			    })
			}
	});


});
