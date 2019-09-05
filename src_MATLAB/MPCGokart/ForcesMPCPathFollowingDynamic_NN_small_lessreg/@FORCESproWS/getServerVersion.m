function serverVersionString = getServerVersion(obj)
%getServerVersion(obj)
%
%     Output:
%       serverVersionString = (string)

% Build up the argument lists.
values = { };
names  = { };
types = { };
 % Create the message, make the call, and convert the response into a variable.
soapMessage = createSoapMessage( ...
    'FORCESPro', ...
    'getServerVersion', ...
    values,names,types,'document');
response = callSoapService( ...
    obj.endpoint, ...
    'FORCESPro/getServerVersion', ...
    soapMessage);
serverVersionString = parseSoapResponse(response);
