您好{{name}}，晚上好！您目前余额：{{money}}元，积分：{{point}} 行结尾
行开始 {{if has}}余额充足
{{each item in list}}
{{item.name}}:{{item.price}}
{{/each}}
{{/if}}
