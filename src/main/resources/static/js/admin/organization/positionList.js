document.addEventListener("DOMContentLoaded", function () {
    var createModal = document.getElementById("createModal");
    var editModal = document.getElementById("editModal");
    var openModalBtn = document.getElementById("openModal");
    var closeButtons = document.getElementsByClassName("close");
    var createForm = document.getElementById("positionForm");
    var editForm = document.getElementById("editForm");
    var csrfToken = document.querySelector('input[name="_csrf"]').value;

    if (openModalBtn) {
        openModalBtn.onclick = function () {
            createModal.style.display = "block";
        };
    }

    // 수정
    document.querySelectorAll("button#editButton").forEach(function (editButton) {
        editButton.onclick = function () {
            var positionId = this.getAttribute("data-position-id");

            $.ajax({
                type: "GET",
                url: `/position/get?id=${positionId}`,
                success: function (response) {
                    if (response.res_code === "200") {
                        const position = response.position;
                        document.getElementById("editPositionId").value = position.position_no;
                        document.getElementById("editPositionName").value = position.position_name;
                        document.getElementById("editPositionHigh").value = position.position_high;

                        editModal.style.display = "block";
                    } else {
                        Swal.fire("오류", response.res_msg, "error");
                    }
                },
                error: function () {
                    Swal.fire("오류", "서버 요청 중 오류가 발생했습니다.", "error");
                }
            });
        };
    });

    // 삭제
    document.querySelectorAll("button#deleteButton").forEach(function (deleteButton) {
        deleteButton.onclick = function () {
            var positionId = this.getAttribute("data-position-id");

            Swal.fire({
                title: '직위 삭제',
                text: '직위를 삭제하시겠습니까?',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonColor: '#3085d6',
                cancelButtonColor: '#d33',
                confirmButtonText: '삭제',
                cancelButtonText: '취소'
            }).then((result) => {
                if (result.isConfirmed) {
                    $.ajax({
                        type: "POST",
                        url: "/position/delete",
                        data: { id: positionId },
                        headers: {
                            'X-CSRF-TOKEN': csrfToken
                        },
                        success: function (response) {
                            if (response.res_code === "200") {
                                Swal.fire('삭제 완료', response.res_msg, 'success').then(() => {
                                    location.reload();
                                });
                            } else {
                                Swal.fire('삭제 실패', response.res_msg, 'error');
                            }
                        },
                        error: function () {
                            Swal.fire('서버 오류', "서버 요청 중 오류가 발생했습니다.", 'error');
                        }
                    });
                }
            });
        };
    });

    Array.from(closeButtons).forEach(function (closeButton) {
        closeButton.onclick = closeModal;
    });

    window.onclick = function (event) {
        if (event.target == createModal || event.target == editModal) {
            closeModal();
        }
    };

    function closeModal() {
        createModal.style.display = "none";
        editModal.style.display = "none";
        resetForm(createForm);
        resetForm(editForm);
    }

    function resetForm(form) {
        form.reset();
    }

    // 등록
    if (createForm) {
        createForm.onsubmit = function (event) {
            event.preventDefault();
            var positionName = document.getElementById("positionName").value;
            var positionHigh = document.getElementById("positionHigh").value;

            $.ajax({
                type: "POST",
                url: "/position/add",
                contentType: "application/json",
                data: JSON.stringify({
                    positionName: positionName,
                    positionHigh: positionHigh
                }),
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                },
                success: function (response) {
                    if (response.res_code === "200") {
                        Swal.fire('등록 성공', response.res_msg, 'success').then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire('등록 실패', response.res_msg, 'error');
                    }
                },
                error: function () {
                    Swal.fire('서버 오류', "서버 요청 중 오류가 발생했습니다.", 'error');
                }
            });
        };
    }

    // 수정
    if (editForm) {
        editForm.onsubmit = function (event) {
            event.preventDefault();

            var positionId = document.getElementById("editPositionId").value;
            var positionName = document.getElementById("editPositionName").value;
            var positionHigh = document.getElementById("editPositionHigh").value;

            $.ajax({
                type: "POST",
                url: "/position/update",
                contentType: "application/json",
                data: JSON.stringify({
                    positionId: positionId,
                    positionName: positionName,
                    positionHigh: positionHigh
                }),
                headers: {
                    'X-CSRF-TOKEN': csrfToken
                },
                success: function (response) {
                    if (response.res_code === "200") {
                        Swal.fire("수정 성공", response.res_msg, "success").then(() => {
                            location.reload();
                        });
                    } else {
                        Swal.fire("수정 실패", response.res_msg, "error");
                    }
                },
                error: function () {
                    Swal.fire("서버 오류", "서버 요청 중 오류가 발생했습니다.", "error");
                }
            });
        };
    }
});