$(document).ready(() => {
    let email = $('#email').val();
    let provider = $('#provider').val();

    let confirmMessage = `${email}로 가입된 회원이 없습니다.\n${provider} 간편 가입을 진행하시겠습니까?`;
    if (confirm(confirmMessage)) {
        $('#email').val(email); // 이메일 자동 채움
        $('#signupForm').show();  // 폼을 표시합니다.

        $('.role-button').click(function() {
            let role = $(this).data('role');
            $('#role').val(role);
            if (role === 'seller') {
                $('#businessNumberContainer').show();
            } else {
                $('#businessNumberContainer').hide();
            }
        });

        $('#signup').click(() => {
            let formData = {
                email: email,
                nickname: $('#nickname').val(),
                phone: $('#phone').val(),
                provider: provider,
                role: $('#role').val()
            };

            if ($('#role').val() === 'seller') {
                formData.businessNumber = $('#businessNumber').val();
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
        window.location.href = '/'; // 취소 시 홈으로 리디렉션
    }
});
