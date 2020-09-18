$(function(){

        const appendBusiness = function(data){
            var businessCode = '<a href="#" class="business-link" data-id="' + data.id + '">' + data.name + '</a><br>';
            $('#business-list').append('<div>' + businessCode + '</div>');
        }

        $('#save-business').click(function()
        {
            var data = $('#business-form form').serialize();
            $.ajax({
                method: "POST",
                url: '/business/',
                data: data,
                success: function(response)
                {
                    var business = {};
                    business.id = response.id;
                    var dataArray = $('#business-form form').serializeArray();
                    for(i in dataArray) {
                        business[dataArray[i]['name']] = dataArray[i]['value'];
                    }
                    appendBusiness(business);
                }
            });
            return false;
        });
});