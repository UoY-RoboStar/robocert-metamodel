SERVER=us16
# robocert-metamodel-update
TID=10026378
LID=234a3b02e7 # test list
# LID=a67ed5ba2c # real RoboTool list
DATE=$(date +"%d%h%Y-%H:%M:%S")
CREATION_DATA='{"type":"regular","recipients":{"list_id":"'$LID'"},"settings":{"subject_line":"RoboCert Metamodel Update","preview_text":"The RoboCert Metamodel plugins have been updated.","title":"robocert-metamodel-plugin-'${DATE}'","from_name":"RoboTool","reply_to":"robotool@barom.org.uk","auto_tweet":false,"auto_fb_post":[],"fb_comments":false,"template_id":'${TID}'},"content_type":"template"}'

# shellcheck disable=SC2153
echo "$DATA" | jq -r

RESULT=$(curl -Ss -X POST \
  https://${SERVER}.api.mailchimp.com/3.0/campaigns \
  --user "$MAILCHIMPUSR:$MAILCHIMPKEY" \
  -d "${CREATION_DATA}")
  
echo "Result is${RESULT}"
  
CID=$(echo "$RESULT" | jq -r '.id')

echo "Sending campaign with id $CID"

curl -Ss -X POST \
  "https://${SERVER}.api.mailchimp.com/3.0/campaigns/${CID}/actions/send" \
  --user "$MAILCHIMPUSR:$MAILCHIMPKEY"
