CreativeItemControl is a plugin designed to allow you to prevent playters from obtaining or using impossible items, such as..

Impossible enchantments:
- Incompatible (Infinity + mending)
- Impossible levels (Sharpness 10)
- Incompatible items (Sharpness on a stick)

Custom potions: 
- Potions with custom effects

Attribute Modifiers:
- Prevent any items with attributre modifiers

You can also allow specific items with the /cic exclude commmand.

Commands:

/cic exclude <id> | Exclude held item from CIC. Id is any name you wish to give it
/cic give <id> [player] | Give yourself or another player (if you have permissions) an excluded item
/cic list | List available excluded items
/cic reload | Reload the plugin config
/cic remove <id> | Remove an excluded item from exclusions list

Permissions:

cic.bypass.enchantments - Bypass enchantment protections
cic.bypass.potions - Bypass potion protections
cic.bypass.attributes - Bypass attrribute protections
cic.bypass.components - Bypass component protections
cic.bypass.* - Bypass all protections

cic.give - Allow a player to use /cic give on themself
cic.admin - Admin commands (/cic exclude, reload, remove), allow /cic give on others

Multiple action options for dealing with the items, configurable in the config.


Plans:

- Allow whitelisting of attribute modifiers, enchantments, or components in config
