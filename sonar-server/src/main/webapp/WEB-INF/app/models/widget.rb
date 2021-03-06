#
# Sonar, entreprise quality control tool.
# Copyright (C) 2008-2011 SonarSource
# mailto:contact AT sonarsource DOT com
#
# Sonar is free software; you can redistribute it and/or
# modify it under the terms of the GNU Lesser General Public
# License as published by the Free Software Foundation; either
# version 3 of the License, or (at your option) any later version.
#
# Sonar is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
# Lesser General Public License for more details.
#
# You should have received a copy of the GNU Lesser General Public
# License along with Sonar; if not, write to the Free Software
# Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
#
class Widget < ActiveRecord::Base
  has_many :properties, :dependent => :delete_all, :class_name => 'WidgetProperty'
  belongs_to :dashboards

  validates_presence_of :name
  validates_length_of :name, :within => 1..256

  validates_presence_of :widget_key
  validates_length_of :widget_key, :within => 1..256

  def property(key)
    self.properties().each do |p|
      return p if (p.key==key)
    end
    nil
  end

  def key
    widget_key
  end

  def html_id
    "block_#{id}"
  end

  def property_text_value(key)
    prop=property(key)
    prop ? prop.text_value : nil
  end

  def property_value(key)
    prop=property(key)
    prop ? prop.value : nil
  end

  def properties_as_hash
    @properties_hash ||=
      begin
        hash={}
        java_definition.getWidgetProperties().each do |property_definition|
          prop = property(property_definition.key)
          if prop
            hash[property_definition.key]=prop.value
          elsif !property_definition.defaultValue().blank?
            hash[property_definition.key]=WidgetProperty.text_to_value(property_definition.defaultValue(), property_definition.type().name())
          end
        end
        hash
      end
  end

  def java_definition
    Java::OrgSonarServerUi::JRubyFacade.getInstance().getWidget(key)
  end

  def layout
    java_definition.getWidgetLayout().name()
  end
end