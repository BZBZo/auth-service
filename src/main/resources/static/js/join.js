$(document).ready(() => {
    let email = $('#email').val();
    let provider = $('#provider').val();

    let confirmMessage = `${email}로 가입된 회원이 없습니다.\n${provider} 간편 가입을 진행하시겠습니까?`;
    if (confirm(confirmMessage)) {
        $('#signupForm').show();  // 폼을 표시합니다.

        // 이벤트 위임을 사용하여 role-button에 클릭 이벤트를 바인딩
        $('#signupForm').on('click', '.role-button', function() {
            $('.role-button').css('background-color', ''); // 다른 모든 버튼의 배경색을 초기화
            $(this).css('background-color', '#4CAF50'); // 현재 클릭된 버튼의 배경색 변경

            let role = $(this).data('role');
            $('#role').val(role);

            if (role === 'ROLE_SELLER') {
                $('#sellerContainer').show();
                $('#customerContainer').hide();
            } else {
                $('#sellerContainer').hide();
                $('#customerContainer').show();
            }
        });

        $('#signup').click(() => {
            let formData = {
                email: email,
                provider: provider,
                role: $('#role').val()
            };

            if ($('#role').val() === 'ROLE_SELLER') {
                formData.businessNumber = $('#businessNumber').val();
                formData.nickname = $('#shopName').val();
                formData.phone = $('#shopPhone').val();
            }

            if ($('#role').val() === 'ROLE_CUSTOMER') {
                formData.nickname = $('#nickname').val();
                formData.phone = $('#customerPhone').val();
            }

            $.ajax({
                type: 'POST',
                url: '/join',
                data: JSON.stringify(formData),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                success: function(response) {
                    alert('회원가입이 성공했습니다.\n로그인해주세요.');
                    window.location.href = response.url;
                },
                error: function(error) {
                    console.error('오류 발생:', error);
                    alert('회원가입 중 오류가 발생했습니다.');
                }
            });
        });
    } else {
        window.location.href = '/welcome'; // 취소 시 홈으로 리디렉션
    }

    $('#cancelButton').click(function() {
        window.location.href = '/welcome'; // 취소 버튼 클릭 시 /welcome으로 이동
    });
});
