姓名：{{name}}，性别：{{if sex==1}}男{{else if sex==2}}女{{else}}未知{{/if}}，您目前余额：{{money}}元，积分：{{point}} 行结尾
行开始 {{if has}}余额充足
{{each item in list}}
{{item.name}}:{{item.price}}
{{/each}}
{{else}}
余额不足
{{/if}}
