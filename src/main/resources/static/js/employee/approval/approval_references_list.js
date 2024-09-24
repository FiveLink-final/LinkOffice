document.addEventListener('DOMContentLoaded', function(){
	const memberRows = document.querySelectorAll('.approval_row');
	
	memberRows.forEach(row => {
		row.addEventListener('click',function(){
			const approvalNo = this.getAttribute('data-approval_no');
			const approvalType = this.getAttribute('data-type');
			console.log(approvalNo);
			console.log(approvalType);
		 if (approvalType === 'VACATION') {
                window.location.href = '/employee/approval/approval_references_vacation_detail/'+approvalNo;
            } else {
                window.location.href = '/employee/approval/approval_references_detail/'+approvalNo;
            }
		
		});
	});
});